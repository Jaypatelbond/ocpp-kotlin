package com.ocpp.core.api

import com.ocpp.core.transport.ConnectionState
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

/**
 * Generic OCPP Client Interface - Version Agnostic.
 *
 * This interface provides a unified API that works with both OCPP 1.6 and 2.0.1,
 * allowing you to write version-independent code. Use this when you need to
 * support multiple OCPP versions without changing your business logic.
 *
 * Example usage:
 * ```kotlin
 * val client: GenericOcppClient = when (version) {
 *     OcppVersion.V16 -> GenericOcpp16Adapter()
 *     OcppVersion.V201 -> GenericOcpp201Adapter()
 * }
 *
 * // Same code works for both versions!
 * client.connect(url, chargePointId)
 * val result = client.bootNotification(
 *     model = "FastCharger",
 *     vendor = "MyCompany"
 * )
 * ```
 */
interface GenericOcppClient {

    /**
     * Current connection state.
     */
    val connectionState: StateFlow<ConnectionState>

    /**
     * OCPP version this client supports.
     */
    val version: OcppVersion

    /**
     * Connect to the CSMS.
     *
     * @param url WebSocket URL of the CSMS
     * @param chargePointId Unique identifier for this charge point
     */
    suspend fun connect(url: String, chargePointId: String)

    /**
     * Disconnect from the CSMS.
     */
    suspend fun disconnect()

    // ==================== Provisioning ====================

    /**
     * Send BootNotification - Register the charging station with the CSMS.
     *
     * @param model Model name of the charging station
     * @param vendor Vendor/manufacturer name
     * @param serialNumber Optional serial number
     * @param firmwareVersion Optional firmware version
     * @return Registration result with status and heartbeat interval
     */
    suspend fun bootNotification(
        model: String,
        vendor: String,
        serialNumber: String? = null,
        firmwareVersion: String? = null
    ): Result<BootNotificationResult>

    /**
     * Send Heartbeat - Keep the connection alive.
     *
     * @return Current time from the CSMS
     */
    suspend fun heartbeat(): Result<HeartbeatResult>

    // ==================== Authorization ====================

    /**
     * Send Authorize - Check if an ID is allowed to charge.
     *
     * @param idToken The identifier (RFID, app user ID, etc.)
     * @return Authorization status
     */
    suspend fun authorize(idToken: String): Result<AuthorizationResult>

    // ==================== Status ====================

    /**
     * Send StatusNotification - Report connector status.
     *
     * @param connectorId Connector ID (1-based, 0 for charge point)
     * @param status Current status
     * @param errorCode Optional error code
     */
    suspend fun statusNotification(
        connectorId: Int,
        status: GenericConnectorStatus,
        errorCode: String? = null
    ): Result<Unit>

    // ==================== Transactions ====================

    /**
     * Start a charging transaction.
     *
     * @param connectorId Connector where charging starts
     * @param idToken ID of the customer
     * @param meterStart Starting meter value (Wh)
     * @return Transaction ID and authorization status
     */
    suspend fun startTransaction(
        connectorId: Int,
        idToken: String,
        meterStart: Int
    ): Result<TransactionResult>

    /**
     * Stop a charging transaction.
     *
     * @param transactionId ID of the transaction to stop
     * @param meterStop Final meter value (Wh)
     * @param reason Optional reason for stopping
     */
    suspend fun stopTransaction(
        transactionId: String,
        meterStop: Int,
        reason: StopReason? = null
    ): Result<Unit>

    /**
     * Send meter values during charging.
     *
     * @param connectorId Connector ID
     * @param transactionId Optional transaction ID
     * @param energyWh Energy delivered in Wh
     * @param powerW Optional current power in W
     */
    suspend fun meterValues(
        connectorId: Int,
        transactionId: String? = null,
        energyWh: Int,
        powerW: Int? = null
    ): Result<Unit>

    // ==================== Remote Command Handlers ====================

    /**
     * Set handler for remote start transaction requests.
     */
    fun onRemoteStart(handler: suspend (RemoteStartRequest) -> RemoteStartResponse)

    /**
     * Set handler for remote stop transaction requests.
     */
    fun onRemoteStop(handler: suspend (RemoteStopRequest) -> RemoteStopResponse)

    /**
     * Set handler for reset requests.
     */
    fun onReset(handler: suspend (ResetRequest) -> ResetResponse)
}

// ==================== Common Data Types ====================

/**
 * OCPP Version.
 */
enum class OcppVersion {
    V16,
    V201
}

/**
 * Generic connector status that maps to both OCPP versions.
 */
enum class GenericConnectorStatus {
    Available,
    Preparing,
    Charging,
    SuspendedEVSE,
    SuspendedEV,
    Finishing,
    Reserved,
    Unavailable,
    Faulted
}

/**
 * Generic stop reason.
 */
enum class StopReason {
    DeAuthorized,
    EmergencyStop,
    EVDisconnected,
    HardReset,
    Local,
    Other,
    PowerLoss,
    Reboot,
    Remote,
    SoftReset,
    UnlockCommand
}

/**
 * Result of BootNotification.
 */
data class BootNotificationResult(
    val accepted: Boolean,
    val heartbeatIntervalSeconds: Int,
    val currentTime: String
)

/**
 * Result of Heartbeat.
 */
data class HeartbeatResult(
    val currentTime: String
)

/**
 * Result of Authorization.
 */
data class AuthorizationResult(
    val accepted: Boolean,
    val expiryDate: String? = null
)

/**
 * Result of StartTransaction.
 */
data class TransactionResult(
    val transactionId: String,
    val authorized: Boolean
)

/**
 * Remote start request.
 */
data class RemoteStartRequest(
    val idToken: String,
    val connectorId: Int? = null
)

/**
 * Remote start response.
 */
data class RemoteStartResponse(
    val accepted: Boolean
)

/**
 * Remote stop request.
 */
data class RemoteStopRequest(
    val transactionId: String
)

/**
 * Remote stop response.
 */
data class RemoteStopResponse(
    val accepted: Boolean
)

/**
 * Reset request.
 */
data class ResetRequest(
    val hard: Boolean
)

/**
 * Reset response.
 */
data class ResetResponse(
    val accepted: Boolean
)
