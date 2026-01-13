package com.ocpp.v16.client

import com.ocpp.core.client.BaseOcppClient
import com.ocpp.core.message.CallResult
import com.ocpp.core.transport.OcppTransport
import com.ocpp.core.transport.OcppTransportConfig
import com.ocpp.core.transport.OkHttpOcppTransport
import com.ocpp.v16.messages.*
import com.ocpp.v16.types.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Type-safe OCPP 1.6 client for Charging Point to Central System communication.
 *
 * This client provides convenient methods for all OCPP 1.6 operations with
 * proper type safety through Kotlin data classes and sealed types.
 *
 * ## Example Usage
 * ```kotlin
 * val client = Ocpp16Client()
 * client.connect("ws://csms.example.com/ocpp/1.6", "CP001")
 *
 * val response = client.bootNotification(
 *     chargePointModel = "Model S",
 *     chargePointVendor = "Tesla"
 * )
 *
 * if (response.isSuccess && response.getOrNull()?.status == RegistrationStatus.Accepted) {
 *     println("Charger registered with ${response.getOrNull()?.interval}s heartbeat")
 * }
 * ```
 *
 * @param transport The transport layer to use for WebSocket communication
 * @param scope Coroutine scope for internal operations
 */
class Ocpp16Client(
    transport: OcppTransport = OkHttpOcppTransport(
        OcppTransportConfig(subProtocols = listOf("ocpp1.6"))
    ),
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : BaseOcppClient(transport, scope) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    // ==================== Core Profile ====================

    /**
     * Sends a BootNotification to the Central System.
     *
     * This should be the first message sent after connecting. It informs the
     * Central System about the Charge Point's model, vendor, and other details.
     *
     * @param chargePointModel Model of the Charge Point
     * @param chargePointVendor Vendor of the Charge Point
     * @param chargeBoxSerialNumber Optional serial number
     * @param chargePointSerialNumber Optional charge point serial number
     * @param firmwareVersion Optional firmware version
     * @param iccid Optional ICCID of the modem's SIM card
     * @param imsi Optional IMSI of the modem's SIM card
     * @param meterSerialNumber Optional serial number of the main electrical meter
     * @param meterType Optional type of the main electrical meter
     * @return BootNotificationResponse with registration status and heartbeat interval
     */
    suspend fun bootNotification(
        chargePointModel: String,
        chargePointVendor: String,
        chargeBoxSerialNumber: String? = null,
        chargePointSerialNumber: String? = null,
        firmwareVersion: String? = null,
        iccid: String? = null,
        imsi: String? = null,
        meterSerialNumber: String? = null,
        meterType: String? = null
    ): Result<BootNotificationResponse> {
        val request = BootNotificationRequest(
            chargePointModel = chargePointModel,
            chargePointVendor = chargePointVendor,
            chargeBoxSerialNumber = chargeBoxSerialNumber,
            chargePointSerialNumber = chargePointSerialNumber,
            firmwareVersion = firmwareVersion,
            iccid = iccid,
            imsi = imsi,
            meterSerialNumber = meterSerialNumber,
            meterType = meterType
        )
        return sendTyped("BootNotification", request)
    }

    /**
     * Sends a Heartbeat to the Central System.
     *
     * The Charge Point should send this at the interval specified in the
     * BootNotificationResponse to indicate it is still alive.
     *
     * @return HeartbeatResponse with the current time from the Central System
     */
    suspend fun heartbeat(): Result<HeartbeatResponse> {
        return sendTyped("Heartbeat", HeartbeatRequest())
    }

    /**
     * Sends a StatusNotification to report connector status.
     *
     * @param connectorId ID of the connector (0 = Charge Point main controller)
     * @param status Current status of the connector
     * @param errorCode Error code if an error occurred
     * @param info Optional additional info
     * @param timestamp Optional timestamp of the status change
     * @param vendorId Optional vendor ID
     * @param vendorErrorCode Optional vendor-specific error code
     * @return StatusNotificationResponse (empty confirmation)
     */
    suspend fun statusNotification(
        connectorId: Int,
        status: ChargePointStatus,
        errorCode: ChargePointErrorCode = ChargePointErrorCode.NoError,
        info: String? = null,
        timestamp: String? = null,
        vendorId: String? = null,
        vendorErrorCode: String? = null
    ): Result<StatusNotificationResponse> {
        val request = StatusNotificationRequest(
            connectorId = connectorId,
            status = status,
            errorCode = errorCode,
            info = info,
            timestamp = timestamp,
            vendorId = vendorId,
            vendorErrorCode = vendorErrorCode
        )
        return sendTyped("StatusNotification", request)
    }

    /**
     * Sends an Authorize request to check if an ID is valid.
     *
     * @param idTag The ID tag to authorize
     * @return AuthorizeResponse with authorization status
     */
    suspend fun authorize(idTag: String): Result<AuthorizeResponse> {
        val request = AuthorizeRequest(idTag = idTag)
        return sendTyped("Authorize", request)
    }

    /**
     * Sends a StartTransaction notification.
     *
     * @param connectorId Connector where transaction started
     * @param idTag ID tag that started the transaction
     * @param meterStart Meter value at start (Wh)
     * @param timestamp Start timestamp
     * @param reservationId Optional reservation ID
     * @return StartTransactionResponse with transaction ID and auth status
     */
    suspend fun startTransaction(
        connectorId: Int,
        idTag: String,
        meterStart: Int,
        timestamp: String,
        reservationId: Int? = null
    ): Result<StartTransactionResponse> {
        val request = StartTransactionRequest(
            connectorId = connectorId,
            idTag = idTag,
            meterStart = meterStart,
            timestamp = timestamp,
            reservationId = reservationId
        )
        return sendTyped("StartTransaction", request)
    }

    /**
     * Sends a StopTransaction notification.
     *
     * @param meterStop Final meter value (Wh)
     * @param timestamp Stop timestamp
     * @param transactionId Transaction ID to stop
     * @param reason Optional reason for stopping
     * @param idTag Optional ID tag
     * @param transactionData Optional transaction data
     * @return StopTransactionResponse with optional auth info update
     */
    suspend fun stopTransaction(
        meterStop: Int,
        timestamp: String,
        transactionId: Int,
        reason: Reason? = null,
        idTag: String? = null,
        transactionData: List<MeterValue>? = null
    ): Result<StopTransactionResponse> {
        val request = StopTransactionRequest(
            meterStop = meterStop,
            timestamp = timestamp,
            transactionId = transactionId,
            reason = reason,
            idTag = idTag,
            transactionData = transactionData
        )
        return sendTyped("StopTransaction", request)
    }

    /**
     * Sends MeterValues during or after a transaction.
     *
     * @param connectorId Connector ID
     * @param meterValue List of meter values
     * @param transactionId Optional transaction ID
     * @return MeterValuesResponse (empty confirmation)
     */
    suspend fun meterValues(
        connectorId: Int,
        meterValue: List<MeterValue>,
        transactionId: Int? = null
    ): Result<MeterValuesResponse> {
        val request = MeterValuesRequest(
            connectorId = connectorId,
            meterValue = meterValue,
            transactionId = transactionId
        )
        return sendTyped("MeterValues", request)
    }

    // ==================== Data Transfer ====================

    /**
     * Sends a DataTransfer message for vendor-specific data.
     *
     * @param vendorId Vendor identifier
     * @param messageId Optional message identifier
     * @param data Optional data payload
     * @return DataTransferResponse with status and optional response data
     */
    suspend fun dataTransfer(
        vendorId: String,
        messageId: String? = null,
        data: String? = null
    ): Result<DataTransferResponse> {
        val request = DataTransferRequest(
            vendorId = vendorId,
            messageId = messageId,
            data = data
        )
        return sendTyped("DataTransfer", request)
    }

    // ==================== Firmware Management ====================

    /**
     * Reports firmware status to the Central System.
     *
     * @param status Current firmware update status
     * @return FirmwareStatusNotificationResponse (empty confirmation)
     */
    suspend fun firmwareStatusNotification(
        status: FirmwareStatus
    ): Result<FirmwareStatusNotificationResponse> {
        val request = FirmwareStatusNotificationRequest(status = status)
        return sendTyped("FirmwareStatusNotification", request)
    }

    /**
     * Reports diagnostics upload status to the Central System.
     *
     * @param status Current diagnostics status
     * @return DiagnosticsStatusNotificationResponse (empty confirmation)
     */
    suspend fun diagnosticsStatusNotification(
        status: DiagnosticsStatus
    ): Result<DiagnosticsStatusNotificationResponse> {
        val request = DiagnosticsStatusNotificationRequest(status = status)
        return sendTyped("DiagnosticsStatusNotification", request)
    }

    // ==================== Central System Message Handlers ====================

    /**
     * Sets a handler for RemoteStartTransaction requests from the Central System.
     */
    fun onRemoteStartTransaction(handler: suspend (RemoteStartTransactionRequest) -> RemoteStartTransactionResponse) {
        onTypedCall("RemoteStartTransaction", handler)
    }

    /**
     * Sets a handler for RemoteStopTransaction requests from the Central System.
     */
    fun onRemoteStopTransaction(handler: suspend (RemoteStopTransactionRequest) -> RemoteStopTransactionResponse) {
        onTypedCall("RemoteStopTransaction", handler)
    }

    /**
     * Sets a handler for Reset requests from the Central System.
     */
    fun onReset(handler: suspend (ResetRequest) -> ResetResponse) {
        onTypedCall("Reset", handler)
    }

    /**
     * Sets a handler for UnlockConnector requests from the Central System.
     */
    fun onUnlockConnector(handler: suspend (UnlockConnectorRequest) -> UnlockConnectorResponse) {
        onTypedCall("UnlockConnector", handler)
    }

    /**
     * Sets a handler for ChangeConfiguration requests from the Central System.
     */
    fun onChangeConfiguration(handler: suspend (ChangeConfigurationRequest) -> ChangeConfigurationResponse) {
        onTypedCall("ChangeConfiguration", handler)
    }

    /**
     * Sets a handler for GetConfiguration requests from the Central System.
     */
    fun onGetConfiguration(handler: suspend (GetConfigurationRequest) -> GetConfigurationResponse) {
        onTypedCall("GetConfiguration", handler)
    }

    /**
     * Sets a handler for SetChargingProfile requests from the Central System.
     */
    fun onSetChargingProfile(handler: suspend (SetChargingProfileRequest) -> SetChargingProfileResponse) {
        onTypedCall("SetChargingProfile", handler)
    }

    /**
     * Sets a handler for ClearChargingProfile requests from the Central System.
     */
    fun onClearChargingProfile(handler: suspend (ClearChargingProfileRequest) -> ClearChargingProfileResponse) {
        onTypedCall("ClearChargingProfile", handler)
    }

    /**
     * Sets a handler for TriggerMessage requests from the Central System.
     */
    fun onTriggerMessage(handler: suspend (TriggerMessageRequest) -> TriggerMessageResponse) {
        onTypedCall("TriggerMessage", handler)
    }

    /**
     * Sets a handler for UpdateFirmware requests from the Central System.
     */
    fun onUpdateFirmware(handler: suspend (UpdateFirmwareRequest) -> UpdateFirmwareResponse) {
        onTypedCall("UpdateFirmware", handler)
    }

    /**
     * Sets a handler for GetDiagnostics requests from the Central System.
     */
    fun onGetDiagnostics(handler: suspend (GetDiagnosticsRequest) -> GetDiagnosticsResponse) {
        onTypedCall("GetDiagnostics", handler)
    }

    // ==================== Helper Methods ====================

    private suspend inline fun <reified Req, reified Resp> sendTyped(
        action: String,
        request: Req,
        timeout: Duration = 30.seconds
    ): Result<Resp> {
        return try {
            val payload = json.encodeToJsonElement(request) as JsonObject
            sendCall(action, payload, timeout).mapCatching { callResult ->
                json.decodeFromJsonElement<Resp>(callResult.payload)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private inline fun <reified Req, reified Resp> onTypedCall(
        action: String,
        crossinline handler: suspend (Req) -> Resp
    ) {
        onCall(action) { call ->
            val request = json.decodeFromJsonElement<Req>(call.payload)
            val response = handler(request)
            val responsePayload = json.encodeToJsonElement(response) as JsonObject
            CallResult(call.messageId, responsePayload)
        }
    }
}
