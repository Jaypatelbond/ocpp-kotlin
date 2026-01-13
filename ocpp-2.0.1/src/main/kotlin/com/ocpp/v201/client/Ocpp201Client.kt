package com.ocpp.v201.client

import com.ocpp.core.client.BaseOcppClient
import com.ocpp.core.client.OcppCallErrorException
import com.ocpp.core.message.Call
import com.ocpp.core.message.CallResult
import com.ocpp.core.transport.OcppCredentials
import com.ocpp.core.transport.OcppTransport
import com.ocpp.core.transport.OkHttpOcppTransport
import com.ocpp.v201.messages.*
import com.ocpp.v201.types.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * OCPP 2.0.1 Client providing type-safe operations for charging station communication.
 *
 * This client wraps the base OCPP client and provides convenience methods for
 * all OCPP 2.0.1 operations with proper serialization/deserialization.
 *
 * Example usage:
 * ```kotlin
 * val client = Ocpp201Client()
 * client.connect("ws://csms.example.com/ocpp", "CP001")
 *
 * val response = client.bootNotification(
 *     chargingStation = ChargingStationType("Model X", "VendorY"),
 *     reason = BootReasonEnumType.PowerUp
 * )
 *
 * if (response.isSuccess) {
 *     val bootResponse = response.getOrThrow()
 *     println("Registered with status: ${bootResponse.status}")
 * }
 * ```
 */
class Ocpp201Client(
    transport: OcppTransport = OkHttpOcppTransport(),
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : BaseOcppClient(transport, scope) {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
        isLenient = true
    }
    
    // ========================================================================
    // Provisioning
    // ========================================================================
    
    /**
     * Send BootNotification to inform CSMS that the charging station has booted.
     */
    suspend fun bootNotification(
        chargingStation: ChargingStationType,
        reason: BootReasonEnumType,
        timeout: Duration = 30.seconds
    ): Result<BootNotificationResponse> {
        val request = BootNotificationRequest(chargingStation, reason)
        return sendTyped(BootNotificationRequest.ACTION, request, timeout)
    }
    
    /**
     * Send Heartbeat to keep the connection alive.
     */
    suspend fun heartbeat(timeout: Duration = 30.seconds): Result<HeartbeatResponse> {
        val request = HeartbeatRequest()
        return sendTyped(HeartbeatRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Authorization
    // ========================================================================
    
    /**
     * Send Authorize request to authorize an idToken.
     */
    suspend fun authorize(
        idToken: IdTokenType,
        certificate: String? = null,
        iso15118CertificateHashData: List<OCSPRequestDataType>? = null,
        timeout: Duration = 30.seconds
    ): Result<AuthorizeResponse> {
        val request = AuthorizeRequest(idToken, certificate, iso15118CertificateHashData)
        return sendTyped(AuthorizeRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Transactions
    // ========================================================================
    
    /**
     * Send TransactionEvent to report transaction events.
     */
    suspend fun transactionEvent(
        eventType: TransactionEventEnumType,
        timestamp: String,
        triggerReason: TriggerReasonEnumType,
        seqNo: Int,
        transactionInfo: TransactionType,
        offline: Boolean? = null,
        numberOfPhasesUsed: Int? = null,
        cableMaxCurrent: Int? = null,
        reservationId: Int? = null,
        evse: EVSEType? = null,
        idToken: IdTokenType? = null,
        meterValue: List<MeterValueType>? = null,
        timeout: Duration = 30.seconds
    ): Result<TransactionEventResponse> {
        val request = TransactionEventRequest(
            eventType, timestamp, triggerReason, seqNo, transactionInfo,
            offline, numberOfPhasesUsed, cableMaxCurrent, reservationId,
            evse, idToken, meterValue
        )
        return sendTyped(TransactionEventRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Availability
    // ========================================================================
    
    /**
     * Send StatusNotification to report connector status.
     */
    suspend fun statusNotification(
        timestamp: String,
        connectorStatus: ConnectorStatusEnumType,
        evseId: Int,
        connectorId: Int,
        timeout: Duration = 30.seconds
    ): Result<StatusNotificationResponse> {
        val request = StatusNotificationRequest(timestamp, connectorStatus, evseId, connectorId)
        return sendTyped(StatusNotificationRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Metering
    // ========================================================================
    
    /**
     * Send MeterValues to report meter readings.
     */
    suspend fun meterValues(
        evseId: Int,
        meterValue: List<MeterValueType>,
        timeout: Duration = 30.seconds
    ): Result<MeterValuesResponse> {
        val request = MeterValuesRequest(evseId, meterValue)
        return sendTyped(MeterValuesRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Firmware
    // ========================================================================
    
    /**
     * Send FirmwareStatusNotification to report firmware update status.
     */
    suspend fun firmwareStatusNotification(
        status: FirmwareStatusEnumType,
        requestId: Int? = null,
        timeout: Duration = 30.seconds
    ): Result<FirmwareStatusNotificationResponse> {
        val request = FirmwareStatusNotificationRequest(status, requestId)
        return sendTyped(FirmwareStatusNotificationRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Diagnostics
    // ========================================================================
    
    /**
     * Send LogStatusNotification to report log upload status.
     */
    suspend fun logStatusNotification(
        status: UploadLogStatusEnumType,
        requestId: Int? = null,
        timeout: Duration = 30.seconds
    ): Result<LogStatusNotificationResponse> {
        val request = LogStatusNotificationRequest(status, requestId)
        return sendTyped(LogStatusNotificationRequest.ACTION, request, timeout)
    }
    
    /**
     * Send NotifyEvent to report monitoring events.
     */
    suspend fun notifyEvent(
        generatedAt: String,
        seqNo: Int,
        eventData: List<EventDataType>,
        tbc: Boolean? = null,
        timeout: Duration = 30.seconds
    ): Result<NotifyEventResponse> {
        val request = NotifyEventRequest(generatedAt, seqNo, eventData, tbc)
        return sendTyped(NotifyEventRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Security
    // ========================================================================
    
    /**
     * Send SecurityEventNotification to report security events.
     */
    suspend fun securityEventNotification(
        type: String,
        timestamp: String,
        techInfo: String? = null,
        timeout: Duration = 30.seconds
    ): Result<SecurityEventNotificationResponse> {
        val request = SecurityEventNotificationRequest(type, timestamp, techInfo)
        return sendTyped(SecurityEventNotificationRequest.ACTION, request, timeout)
    }
    
    /**
     * Send SignCertificate request for certificate signing.
     */
    suspend fun signCertificate(
        csr: String,
        certificateType: CertificateSigningUseEnumType? = null,
        timeout: Duration = 30.seconds
    ): Result<SignCertificateResponse> {
        val request = SignCertificateRequest(csr, certificateType)
        return sendTyped(SignCertificateRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Reservation
    // ========================================================================
    
    /**
     * Send ReservationStatusUpdate to report reservation status changes.
     */
    suspend fun reservationStatusUpdate(
        reservationId: Int,
        reservationUpdateStatus: ReservationUpdateStatusEnumType,
        timeout: Duration = 30.seconds
    ): Result<ReservationStatusUpdateResponse> {
        val request = ReservationStatusUpdateRequest(reservationId, reservationUpdateStatus)
        return sendTyped(ReservationStatusUpdateRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Smart Charging
    // ========================================================================
    
    /**
     * Send NotifyChargingLimit to report charging limits.
     */
    suspend fun notifyChargingLimit(
        chargingLimit: ChargingLimitType,
        chargingSchedule: List<ChargingScheduleType>? = null,
        evseId: Int? = null,
        timeout: Duration = 30.seconds
    ): Result<NotifyChargingLimitResponse> {
        val request = NotifyChargingLimitRequest(chargingLimit, chargingSchedule, evseId)
        return sendTyped(NotifyChargingLimitRequest.ACTION, request, timeout)
    }
    
    /**
     * Send NotifyEVChargingNeeds to report EV charging needs.
     */
    suspend fun notifyEVChargingNeeds(
        evseId: Int,
        chargingNeeds: ChargingNeedsType,
        maxScheduleTuples: Int? = null,
        timeout: Duration = 30.seconds
    ): Result<NotifyEVChargingNeedsResponse> {
        val request = NotifyEVChargingNeedsRequest(evseId, chargingNeeds, maxScheduleTuples)
        return sendTyped(NotifyEVChargingNeedsRequest.ACTION, request, timeout)
    }
    
    /**
     * Send ReportChargingProfiles to report charging profiles.
     */
    suspend fun reportChargingProfiles(
        requestId: Int,
        chargingLimitSource: ChargingLimitSourceEnumType,
        evseId: Int,
        chargingProfile: List<ChargingProfileType>,
        tbc: Boolean? = null,
        timeout: Duration = 30.seconds
    ): Result<ReportChargingProfilesResponse> {
        val request = ReportChargingProfilesRequest(
            requestId, chargingLimitSource, evseId, chargingProfile, tbc
        )
        return sendTyped(ReportChargingProfilesRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // Data Transfer
    // ========================================================================
    
    /**
     * Send DataTransfer for custom vendor-specific data.
     */
    suspend fun dataTransfer(
        vendorId: String,
        messageId: String? = null,
        data: String? = null,
        timeout: Duration = 30.seconds
    ): Result<DataTransferResponse> {
        val request = DataTransferRequest(vendorId, messageId, data)
        return sendTyped(DataTransferRequest.ACTION, request, timeout)
    }
    
    // ========================================================================
    // CSMS-initiated message handlers
    // ========================================================================
    
    /**
     * Register handler for Reset requests from CSMS.
     */
    fun onReset(handler: suspend (ResetRequest) -> ResetResponse) {
        onTypedCall(ResetRequest.ACTION, handler)
    }
    
    /**
     * Register handler for ChangeAvailability requests from CSMS.
     */
    fun onChangeAvailability(handler: suspend (ChangeAvailabilityRequest) -> ChangeAvailabilityResponse) {
        onTypedCall(ChangeAvailabilityRequest.ACTION, handler)
    }
    
    /**
     * Register handler for GetVariables requests from CSMS.
     */
    fun onGetVariables(handler: suspend (GetVariablesRequest) -> GetVariablesResponse) {
        onTypedCall(GetVariablesRequest.ACTION, handler)
    }
    
    /**
     * Register handler for SetVariables requests from CSMS.
     */
    fun onSetVariables(handler: suspend (SetVariablesRequest) -> SetVariablesResponse) {
        onTypedCall(SetVariablesRequest.ACTION, handler)
    }
    
    /**
     * Register handler for GetBaseReport requests from CSMS.
     */
    fun onGetBaseReport(handler: suspend (GetBaseReportRequest) -> GetBaseReportResponse) {
        onTypedCall(GetBaseReportRequest.ACTION, handler)
    }
    
    /**
     * Register handler for RequestStartTransaction requests from CSMS.
     */
    fun onRequestStartTransaction(handler: suspend (RequestStartTransactionRequest) -> RequestStartTransactionResponse) {
        onTypedCall(RequestStartTransactionRequest.ACTION, handler)
    }
    
    /**
     * Register handler for RequestStopTransaction requests from CSMS.
     */
    fun onRequestStopTransaction(handler: suspend (RequestStopTransactionRequest) -> RequestStopTransactionResponse) {
        onTypedCall(RequestStopTransactionRequest.ACTION, handler)
    }
    
    /**
     * Register handler for TriggerMessage requests from CSMS.
     */
    fun onTriggerMessage(handler: suspend (TriggerMessageRequest) -> TriggerMessageResponse) {
        onTypedCall(TriggerMessageRequest.ACTION, handler)
    }
    
    /**
     * Register handler for UnlockConnector requests from CSMS.
     */
    fun onUnlockConnector(handler: suspend (UnlockConnectorRequest) -> UnlockConnectorResponse) {
        onTypedCall(UnlockConnectorRequest.ACTION, handler)
    }
    
    /**
     * Register handler for SetChargingProfile requests from CSMS.
     */
    fun onSetChargingProfile(handler: suspend (SetChargingProfileRequest) -> SetChargingProfileResponse) {
        onTypedCall(SetChargingProfileRequest.ACTION, handler)
    }
    
    /**
     * Register handler for ClearChargingProfile requests from CSMS.
     */
    fun onClearChargingProfile(handler: suspend (ClearChargingProfileRequest) -> ClearChargingProfileResponse) {
        onTypedCall(ClearChargingProfileRequest.ACTION, handler)
    }
    
    /**
     * Register handler for GetChargingProfiles requests from CSMS.
     */
    fun onGetChargingProfiles(handler: suspend (GetChargingProfilesRequest) -> GetChargingProfilesResponse) {
        onTypedCall(GetChargingProfilesRequest.ACTION, handler)
    }
    
    /**
     * Register handler for GetCompositeSchedule requests from CSMS.
     */
    fun onGetCompositeSchedule(handler: suspend (GetCompositeScheduleRequest) -> GetCompositeScheduleResponse) {
        onTypedCall(GetCompositeScheduleRequest.ACTION, handler)
    }
    
    /**
     * Register handler for UpdateFirmware requests from CSMS.
     */
    fun onUpdateFirmware(handler: suspend (UpdateFirmwareRequest) -> UpdateFirmwareResponse) {
        onTypedCall(UpdateFirmwareRequest.ACTION, handler)
    }
    
    /**
     * Register handler for GetLog requests from CSMS.
     */
    fun onGetLog(handler: suspend (GetLogRequest) -> GetLogResponse) {
        onTypedCall(GetLogRequest.ACTION, handler)
    }
    
    /**
     * Register handler for ReserveNow requests from CSMS.
     */
    fun onReserveNow(handler: suspend (ReserveNowRequest) -> ReserveNowResponse) {
        onTypedCall(ReserveNowRequest.ACTION, handler)
    }
    
    /**
     * Register handler for CancelReservation requests from CSMS.
     */
    fun onCancelReservation(handler: suspend (CancelReservationRequest) -> CancelReservationResponse) {
        onTypedCall(CancelReservationRequest.ACTION, handler)
    }
    
    /**
     * Register handler for DataTransfer requests from CSMS.
     */
    fun onDataTransfer(handler: suspend (DataTransferRequest) -> DataTransferResponse) {
        onTypedCall(DataTransferRequest.ACTION, handler)
    }
    
    /**
     * Register handler for CertificateSigned requests from CSMS.
     */
    fun onCertificateSigned(handler: suspend (CertificateSignedRequest) -> CertificateSignedResponse) {
        onTypedCall(CertificateSignedRequest.ACTION, handler)
    }
    
    /**
     * Register handler for InstallCertificate requests from CSMS.
     */
    fun onInstallCertificate(handler: suspend (InstallCertificateRequest) -> InstallCertificateResponse) {
        onTypedCall(InstallCertificateRequest.ACTION, handler)
    }
    
    /**
     * Register handler for DeleteCertificate requests from CSMS.
     */
    fun onDeleteCertificate(handler: suspend (DeleteCertificateRequest) -> DeleteCertificateResponse) {
        onTypedCall(DeleteCertificateRequest.ACTION, handler)
    }
    
    /**
     * Register handler for GetInstalledCertificateIds requests from CSMS.
     */
    fun onGetInstalledCertificateIds(handler: suspend (GetInstalledCertificateIdsRequest) -> GetInstalledCertificateIdsResponse) {
        onTypedCall(GetInstalledCertificateIdsRequest.ACTION, handler)
    }
    
    /**
     * Register handler for SetDisplayMessage requests from CSMS.
     */
    fun onSetDisplayMessage(handler: suspend (SetDisplayMessageRequest) -> SetDisplayMessageResponse) {
        onTypedCall(SetDisplayMessageRequest.ACTION, handler)
    }
    
    /**
     * Register handler for GetDisplayMessages requests from CSMS.
     */
    fun onGetDisplayMessages(handler: suspend (GetDisplayMessagesRequest) -> GetDisplayMessagesResponse) {
        onTypedCall(GetDisplayMessagesRequest.ACTION, handler)
    }
    
    /**
     * Register handler for ClearDisplayMessage requests from CSMS.
     */
    fun onClearDisplayMessage(handler: suspend (ClearDisplayMessageRequest) -> ClearDisplayMessageResponse) {
        onTypedCall(ClearDisplayMessageRequest.ACTION, handler)
    }
    
    /**
     * Register handler for CostUpdated requests from CSMS.
     */
    fun onCostUpdated(handler: suspend (CostUpdatedRequest) -> CostUpdatedResponse) {
        onTypedCall(CostUpdatedRequest.ACTION, handler)
    }
    
    /**
     * Register handler for SendLocalList requests from CSMS.
     */
    fun onSendLocalList(handler: suspend (SendLocalListRequest) -> SendLocalListResponse) {
        onTypedCall(SendLocalListRequest.ACTION, handler)
    }
    
    /**
     * Register handler for GetLocalListVersion requests from CSMS.
     */
    fun onGetLocalListVersion(handler: suspend (GetLocalListVersionRequest) -> GetLocalListVersionResponse) {
        onTypedCall(GetLocalListVersionRequest.ACTION, handler)
    }
    
    /**
     * Register handler for ClearCache requests from CSMS.
     */
    fun onClearCache(handler: suspend (ClearCacheRequest) -> ClearCacheResponse) {
        onTypedCall(ClearCacheRequest.ACTION, handler)
    }
    
    // ========================================================================
    // Internal helpers
    // ========================================================================
    
    private suspend inline fun <reified Req : Ocpp201Request, reified Res : Ocpp201Response> sendTyped(
        action: String,
        request: Req,
        timeout: Duration
    ): Result<Res> {
        val payload = json.encodeToJsonElement(request) as JsonObject
        
        return sendCall(action, payload, timeout).mapCatching { callResult ->
            json.decodeFromJsonElement<Res>(callResult.payload)
        }
    }
    
    private inline fun <reified Req : Ocpp201Request, reified Res : Ocpp201Response> onTypedCall(
        action: String,
        crossinline handler: suspend (Req) -> Res
    ) {
        onCall(action) { call ->
            val request = json.decodeFromJsonElement<Req>(call.payload)
            val response = handler(request)
            val responsePayload = json.encodeToJsonElement(response) as JsonObject
            CallResult(call.messageId, responsePayload)
        }
    }
}
