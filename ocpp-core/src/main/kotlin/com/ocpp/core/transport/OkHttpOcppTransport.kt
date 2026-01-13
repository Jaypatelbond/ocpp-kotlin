package com.ocpp.core.transport

import com.ocpp.core.message.OcppMessage
import com.ocpp.core.message.OcppMessageParser
import com.ocpp.core.message.OcppParseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.min

/**
 * WebSocket-based implementation of OcppTransport using OkHttp.
 *
 * @param config Transport configuration
 * @param scope CoroutineScope for managing coroutines (defaults to IO dispatcher)
 */
class OkHttpOcppTransport(
    private val config: OcppTransportConfig = OcppTransportConfig(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : OcppTransport {
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val incomingMessages = Channel<OcppMessage>(Channel.BUFFERED)
    
    private var webSocket: WebSocket? = null
    private var currentUrl: String? = null
    private var currentChargePointId: String? = null
    private var currentCredentials: OcppCredentials? = null
    private var reconnectAttempt = 0
    
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeoutMs, TimeUnit.MILLISECONDS)
            .pingInterval(config.pingIntervalMs, TimeUnit.MILLISECONDS)
            .build()
    }
    
    override suspend fun connect(
        url: String,
        chargePointId: String,
        credentials: OcppCredentials?
    ) {
        currentUrl = url
        currentChargePointId = chargePointId
        currentCredentials = credentials
        reconnectAttempt = 0
        
        doConnect(url, chargePointId, credentials)
    }
    
    private suspend fun doConnect(
        url: String,
        chargePointId: String,
        credentials: OcppCredentials?
    ) = withContext(Dispatchers.IO) {
        _connectionState.value = ConnectionState.Connecting
        
        try {
            suspendCancellableCoroutine { continuation ->
                val fullUrl = buildFullUrl(url, chargePointId)
                
                val requestBuilder = Request.Builder()
                    .url(fullUrl)
                
                // Add OCPP subprotocols
                config.subProtocols.forEach { protocol ->
                    requestBuilder.addHeader("Sec-WebSocket-Protocol", protocol)
                }
                
                // Add authentication
                when (credentials) {
                    is OcppCredentials.Basic -> {
                        requestBuilder.addHeader(
                            "Authorization",
                            Credentials.basic(credentials.username, credentials.password)
                        )
                    }
                    is OcppCredentials.Certificate -> {
                        // Certificate auth is handled at the OkHttpClient level
                        // TODO: Implement certificate-based authentication
                    }
                    null -> { /* No auth */ }
                }
                
                val request = requestBuilder.build()
                
                webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        _connectionState.value = ConnectionState.Connected
                        reconnectAttempt = 0
                        if (continuation.isActive) {
                            continuation.resume(Unit)
                        }
                    }
                    
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        scope.launch {
                            try {
                                val message = OcppMessageParser.parse(text)
                                incomingMessages.send(message)
                            } catch (e: OcppParseException) {
                                // Log parsing error but don't crash
                                // In production, you might want to send a CallError back
                            }
                        }
                    }
                    
                    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        webSocket.close(code, reason)
                    }
                    
                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        handleDisconnection(null)
                    }
                    
                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        val error = OcppTransportException("WebSocket connection failed: ${t.message}", t)
                        _connectionState.value = ConnectionState.Error(error)
                        
                        if (continuation.isActive) {
                            // Resume normally instead of throwing - let the UI handle the error state
                            continuation.resume(Unit)
                        }
                        
                        // Attempt reconnection if enabled
                        if (config.autoReconnect) {
                            attemptReconnection()
                        }
                    }
                })
                
                continuation.invokeOnCancellation {
                    webSocket?.cancel()
                }
            }
        } catch (e: Exception) {
            // Handle any unexpected exceptions gracefully
            _connectionState.value = ConnectionState.Error(
                OcppTransportException("Connection failed: ${e.message}", e)
            )
        }
    }
    
    private fun buildFullUrl(baseUrl: String, chargePointId: String): String {
        val cleanUrl = baseUrl.trimEnd('/')
        return "$cleanUrl/$chargePointId"
    }
    
    private fun handleDisconnection(error: Throwable?) {
        webSocket = null
        
        if (error != null) {
            _connectionState.value = ConnectionState.Error(error)
        } else {
            _connectionState.value = ConnectionState.Disconnected
        }
        
        // Attempt reconnection if enabled
        if (config.autoReconnect && error != null) {
            attemptReconnection()
        }
    }
    
    private fun attemptReconnection() {
        val url = currentUrl ?: return
        val chargePointId = currentChargePointId ?: return
        
        if (reconnectAttempt >= config.maxReconnectAttempts) {
            _connectionState.value = ConnectionState.Error(
                OcppTransportException("Max reconnection attempts ($config.maxReconnectAttempts) exceeded")
            )
            return
        }
        
        reconnectAttempt++
        _connectionState.value = ConnectionState.Reconnecting(reconnectAttempt, config.maxReconnectAttempts)
        
        scope.launch {
            val delay = calculateReconnectDelay()
            delay(delay)
            
            try {
                doConnect(url, chargePointId, currentCredentials)
            } catch (e: Exception) {
                // Will trigger another reconnection attempt via onFailure
            }
        }
    }
    
    private fun calculateReconnectDelay(): Long {
        val exponentialDelay = config.reconnectDelayMs * 
            Math.pow(config.reconnectBackoffMultiplier, (reconnectAttempt - 1).toDouble())
        return min(exponentialDelay.toLong(), config.maxReconnectDelayMs)
    }
    
    override suspend fun disconnect() {
        webSocket?.close(1000, "Client disconnecting")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
        currentUrl = null
        currentChargePointId = null
        currentCredentials = null
    }
    
    override suspend fun send(message: OcppMessage) {
        val ws = webSocket ?: throw OcppTransportException("Not connected to CSMS")
        
        val jsonMessage = OcppMessageParser.serialize(message)
        val success = ws.send(jsonMessage)
        
        if (!success) {
            throw OcppTransportException("Failed to send message - WebSocket queue full or closed")
        }
    }
    
    override fun incoming(): Flow<OcppMessage> = incomingMessages.receiveAsFlow()
}
