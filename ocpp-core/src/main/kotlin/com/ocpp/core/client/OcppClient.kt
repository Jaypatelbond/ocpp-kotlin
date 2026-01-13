package com.ocpp.core.client

import com.ocpp.core.message.Call
import com.ocpp.core.message.CallError
import com.ocpp.core.message.CallResult
import com.ocpp.core.message.ErrorCode
import com.ocpp.core.message.OcppMessage
import com.ocpp.core.transport.ConnectionState
import com.ocpp.core.transport.OcppCredentials
import com.ocpp.core.transport.OcppTransport
import com.ocpp.core.transport.OcppTransportConfig
import com.ocpp.core.transport.OkHttpOcppTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonObject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Base OCPP client interface providing core functionality for communication with CSMS.
 */
interface OcppClient {
    
    /**
     * Current connection state.
     */
    val connectionState: StateFlow<ConnectionState>
    
    /**
     * Connect to a CSMS.
     *
     * @param url The WebSocket URL of the CSMS
     * @param chargePointId The unique identifier for this charge point
     * @param credentials Optional authentication credentials
     */
    suspend fun connect(
        url: String,
        chargePointId: String,
        credentials: OcppCredentials? = null
    )
    
    /**
     * Disconnect from the CSMS.
     */
    suspend fun disconnect()
    
    /**
     * Send a Call message and wait for the response.
     *
     * @param action The OCPP action name
     * @param payload The request payload
     * @param timeout Timeout for receiving response
     * @return Result containing either CallResult or CallError
     */
    suspend fun sendCall(
        action: String,
        payload: JsonObject,
        timeout: Duration = 30.seconds
    ): Result<CallResult>
    
    /**
     * Send a CallResult in response to a received Call.
     *
     * @param messageId The messageId from the original Call
     * @param payload The response payload
     */
    suspend fun sendCallResult(messageId: String, payload: JsonObject)
    
    /**
     * Send a CallError in response to a received Call.
     *
     * @param messageId The messageId from the original Call
     * @param errorCode The error code
     * @param errorDescription Human-readable error description
     * @param errorDetails Optional additional error details
     */
    suspend fun sendCallError(
        messageId: String,
        errorCode: ErrorCode,
        errorDescription: String,
        errorDetails: JsonObject? = null
    )
    
    /**
     * Register a handler for incoming Call messages.
     *
     * @param action The OCPP action to handle
     * @param handler The handler function that receives the Call and returns a response
     */
    fun onCall(action: String, handler: suspend (Call) -> OcppMessage)
}

/**
 * Base implementation of OcppClient.
 */
open class BaseOcppClient(
    private val transport: OcppTransport = OkHttpOcppTransport(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : OcppClient {
    
    override val connectionState: StateFlow<ConnectionState> = transport.connectionState
    
    // Pending requests waiting for responses
    private val pendingRequests = ConcurrentHashMap<String, PendingRequest>()
    
    // Registered handlers for incoming calls
    private val callHandlers = ConcurrentHashMap<String, suspend (Call) -> OcppMessage>()
    
    // Flow for unhandled incoming calls
    private val unhandledCalls = MutableSharedFlow<Call>()
    
    init {
        // Start processing incoming messages
        scope.launch {
            transport.incoming().collect { message ->
                handleIncomingMessage(message)
            }
        }
    }
    
    private suspend fun handleIncomingMessage(message: OcppMessage) {
        when (message) {
            is Call -> handleIncomingCall(message)
            is CallResult -> handleIncomingCallResult(message)
            is CallError -> handleIncomingCallError(message)
        }
    }
    
    private suspend fun handleIncomingCall(call: Call) {
        val handler = callHandlers[call.action]
        
        if (handler != null) {
            try {
                val response = handler(call)
                transport.send(response)
            } catch (e: Exception) {
                // Send error response
                val error = CallError(
                    messageId = call.messageId,
                    errorCode = ErrorCode.InternalError,
                    errorDescription = "Handler error: ${e.message}"
                )
                transport.send(error)
            }
        } else {
            // Emit to unhandled flow or send NotImplemented error
            val error = CallError(
                messageId = call.messageId,
                errorCode = ErrorCode.NotImplemented,
                errorDescription = "Action '${call.action}' is not implemented"
            )
            transport.send(error)
        }
    }
    
    private fun handleIncomingCallResult(callResult: CallResult) {
        pendingRequests.remove(callResult.messageId)?.let { pending ->
            pending.complete(Result.success(callResult))
        }
    }
    
    private fun handleIncomingCallError(callError: CallError) {
        pendingRequests.remove(callError.messageId)?.let { pending ->
            pending.complete(Result.failure(
                OcppCallErrorException(callError)
            ))
        }
    }
    
    override suspend fun connect(
        url: String,
        chargePointId: String,
        credentials: OcppCredentials?
    ) {
        transport.connect(url, chargePointId, credentials)
    }
    
    override suspend fun disconnect() {
        // Complete all pending requests with error
        pendingRequests.values.forEach { pending ->
            pending.complete(Result.failure(
                OcppClientException("Client disconnected")
            ))
        }
        pendingRequests.clear()
        
        transport.disconnect()
    }
    
    override suspend fun sendCall(
        action: String,
        payload: JsonObject,
        timeout: Duration
    ): Result<CallResult> {
        val messageId = generateMessageId()
        val call = Call(messageId, action, payload)
        
        val pending = PendingRequest()
        pendingRequests[messageId] = pending
        
        return try {
            transport.send(call)
            
            withTimeout(timeout) {
                pending.await()
            }
        } catch (e: TimeoutCancellationException) {
            pendingRequests.remove(messageId)
            Result.failure(OcppTimeoutException("Request timed out after $timeout"))
        } catch (e: Exception) {
            pendingRequests.remove(messageId)
            Result.failure(e)
        }
    }
    
    override suspend fun sendCallResult(messageId: String, payload: JsonObject) {
        val callResult = CallResult(messageId, payload)
        transport.send(callResult)
    }
    
    override suspend fun sendCallError(
        messageId: String,
        errorCode: ErrorCode,
        errorDescription: String,
        errorDetails: JsonObject?
    ) {
        val callError = CallError(messageId, errorCode, errorDescription, errorDetails)
        transport.send(callError)
    }
    
    override fun onCall(action: String, handler: suspend (Call) -> OcppMessage) {
        callHandlers[action] = handler
    }
    
    protected fun generateMessageId(): String = UUID.randomUUID().toString()
}

/**
 * Represents a pending request waiting for a response.
 */
private class PendingRequest {
    private var result: Result<CallResult>? = null
    private var continuation: ((Result<CallResult>) -> Unit)? = null
    
    fun complete(result: Result<CallResult>) {
        synchronized(this) {
            this.result = result
            continuation?.invoke(result)
        }
    }
    
    suspend fun await(): Result<CallResult> = suspendCancellableCoroutine { cont ->
        synchronized(this) {
            result?.let {
                cont.resume(it)
                return@suspendCancellableCoroutine
            }
            continuation = { cont.resume(it) }
        }
    }
}

/**
 * Exception thrown when a CallError is received.
 */
class OcppCallErrorException(
    val callError: CallError
) : Exception("OCPP Error [${callError.errorCode}]: ${callError.errorDescription}")

/**
 * Exception thrown when an OCPP client operation fails.
 */
class OcppClientException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Exception thrown when an OCPP request times out.
 */
class OcppTimeoutException(
    message: String
) : Exception(message)
