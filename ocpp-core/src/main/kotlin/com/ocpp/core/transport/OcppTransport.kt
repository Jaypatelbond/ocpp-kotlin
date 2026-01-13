package com.ocpp.core.transport

import com.ocpp.core.message.OcppMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Transport layer abstraction for OCPP communication.
 * This interface defines the contract for WebSocket-based OCPP transport.
 */
interface OcppTransport {
    
    /**
     * Current connection state as a StateFlow.
     */
    val connectionState: StateFlow<ConnectionState>
    
    /**
     * Connect to the CSMS (Central System Management Server).
     *
     * @param url The WebSocket URL of the CSMS (e.g., "ws://csms.example.com/ocpp")
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
     * Send an OCPP message to the CSMS.
     *
     * @param message The message to send
     * @throws OcppTransportException if sending fails
     */
    suspend fun send(message: OcppMessage)
    
    /**
     * Flow of incoming OCPP messages from the CSMS.
     */
    fun incoming(): Flow<OcppMessage>
}

/**
 * Represents the connection state of the OCPP transport.
 */
sealed class ConnectionState {
    /** Not connected to CSMS */
    data object Disconnected : ConnectionState()
    
    /** Currently attempting to connect */
    data object Connecting : ConnectionState()
    
    /** Successfully connected to CSMS */
    data object Connected : ConnectionState()
    
    /** Connection failed or lost */
    data class Error(val exception: Throwable) : ConnectionState()
    
    /** Attempting to reconnect after connection loss */
    data class Reconnecting(val attempt: Int, val maxAttempts: Int) : ConnectionState()
}

/**
 * Authentication credentials for OCPP connection.
 */
sealed class OcppCredentials {
    /**
     * HTTP Basic Authentication.
     */
    data class Basic(
        val username: String,
        val password: String
    ) : OcppCredentials()
    
    /**
     * TLS Client Certificate Authentication.
     */
    data class Certificate(
        val certificatePath: String,
        val privateKeyPath: String,
        val password: String? = null
    ) : OcppCredentials()
}

/**
 * Configuration for OCPP transport.
 */
data class OcppTransportConfig(
    /** WebSocket ping interval in milliseconds */
    val pingIntervalMs: Long = 30_000L,
    
    /** Connection timeout in milliseconds */
    val connectTimeoutMs: Long = 30_000L,
    
    /** Read timeout in milliseconds */
    val readTimeoutMs: Long = 30_000L,
    
    /** Write timeout in milliseconds */
    val writeTimeoutMs: Long = 30_000L,
    
    /** Enable automatic reconnection */
    val autoReconnect: Boolean = true,
    
    /** Maximum number of reconnection attempts */
    val maxReconnectAttempts: Int = 5,
    
    /** Initial delay before first reconnection attempt in milliseconds */
    val reconnectDelayMs: Long = 1_000L,
    
    /** Maximum delay between reconnection attempts in milliseconds */
    val maxReconnectDelayMs: Long = 60_000L,
    
    /** Multiplier for exponential backoff */
    val reconnectBackoffMultiplier: Double = 2.0,
    
    /** OCPP subprotocols to advertise */
    val subProtocols: List<String> = listOf("ocpp2.0.1", "ocpp1.6")
)

/**
 * Exception thrown when OCPP transport operations fail.
 */
class OcppTransportException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
