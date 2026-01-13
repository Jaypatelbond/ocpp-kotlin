package com.ocpp.v16.messages

import com.ocpp.v16.types.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================================
// Base interfaces for OCPP 1.6 requests and responses
// ============================================================================

interface Ocpp16Request
interface Ocpp16Response

// ============================================================================
// Core Profile Messages
// ============================================================================

/**
 * Authorize request.
 */
@Serializable
data class AuthorizeRequest(
    @SerialName("idTag") val idTag: String
) : Ocpp16Request {
    companion object { const val ACTION = "Authorize" }
}

@Serializable
data class AuthorizeResponse(
    @SerialName("idTagInfo") val idTagInfo: IdTagInfo
) : Ocpp16Response

/**
 * BootNotification request.
 */
@Serializable
data class BootNotificationRequest(
    @SerialName("chargePointVendor") val chargePointVendor: String,
    @SerialName("chargePointModel") val chargePointModel: String,
    @SerialName("chargePointSerialNumber") val chargePointSerialNumber: String? = null,
    @SerialName("chargeBoxSerialNumber") val chargeBoxSerialNumber: String? = null,
    @SerialName("firmwareVersion") val firmwareVersion: String? = null,
    @SerialName("iccid") val iccid: String? = null,
    @SerialName("imsi") val imsi: String? = null,
    @SerialName("meterType") val meterType: String? = null,
    @SerialName("meterSerialNumber") val meterSerialNumber: String? = null
) : Ocpp16Request {
    companion object { const val ACTION = "BootNotification" }
}

@Serializable
data class BootNotificationResponse(
    @SerialName("status") val status: RegistrationStatus,
    @SerialName("currentTime") val currentTime: String,
    @SerialName("interval") val interval: Int
) : Ocpp16Response

/**
 * Heartbeat request.
 */
@Serializable
class HeartbeatRequest : Ocpp16Request {
    companion object { const val ACTION = "Heartbeat" }
}

@Serializable
data class HeartbeatResponse(
    @SerialName("currentTime") val currentTime: String
) : Ocpp16Response

/**
 * StatusNotification request.
 */
@Serializable
data class StatusNotificationRequest(
    @SerialName("connectorId") val connectorId: Int,
    @SerialName("errorCode") val errorCode: ChargePointErrorCode,
    @SerialName("status") val status: ChargePointStatus,
    @SerialName("info") val info: String? = null,
    @SerialName("timestamp") val timestamp: String? = null,
    @SerialName("vendorId") val vendorId: String? = null,
    @SerialName("vendorErrorCode") val vendorErrorCode: String? = null
) : Ocpp16Request {
    companion object { const val ACTION = "StatusNotification" }
}

@Serializable
class StatusNotificationResponse : Ocpp16Response

/**
 * StartTransaction request.
 */
@Serializable
data class StartTransactionRequest(
    @SerialName("connectorId") val connectorId: Int,
    @SerialName("idTag") val idTag: String,
    @SerialName("meterStart") val meterStart: Int,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("reservationId") val reservationId: Int? = null
) : Ocpp16Request {
    companion object { const val ACTION = "StartTransaction" }
}

@Serializable
data class StartTransactionResponse(
    @SerialName("transactionId") val transactionId: Int,
    @SerialName("idTagInfo") val idTagInfo: IdTagInfo
) : Ocpp16Response

/**
 * StopTransaction request.
 */
@Serializable
data class StopTransactionRequest(
    @SerialName("meterStop") val meterStop: Int,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("transactionId") val transactionId: Int,
    @SerialName("idTag") val idTag: String? = null,
    @SerialName("reason") val reason: Reason? = null,
    @SerialName("transactionData") val transactionData: List<MeterValue>? = null
) : Ocpp16Request {
    companion object { const val ACTION = "StopTransaction" }
}

@Serializable
data class StopTransactionResponse(
    @SerialName("idTagInfo") val idTagInfo: IdTagInfo? = null
) : Ocpp16Response

/**
 * MeterValues request.
 */
@Serializable
data class MeterValuesRequest(
    @SerialName("connectorId") val connectorId: Int,
    @SerialName("meterValue") val meterValue: List<MeterValue>,
    @SerialName("transactionId") val transactionId: Int? = null
) : Ocpp16Request {
    companion object { const val ACTION = "MeterValues" }
}

@Serializable
class MeterValuesResponse : Ocpp16Response

/**
 * ChangeAvailability request.
 */
@Serializable
data class ChangeAvailabilityRequest(
    @SerialName("connectorId") val connectorId: Int,
    @SerialName("type") val type: AvailabilityType
) : Ocpp16Request {
    companion object { const val ACTION = "ChangeAvailability" }
}

@Serializable
data class ChangeAvailabilityResponse(
    @SerialName("status") val status: AvailabilityStatus
) : Ocpp16Response

/**
 * Reset request.
 */
@Serializable
data class ResetRequest(
    @SerialName("type") val type: ResetType
) : Ocpp16Request {
    companion object { const val ACTION = "Reset" }
}

@Serializable
data class ResetResponse(
    @SerialName("status") val status: ResetStatus
) : Ocpp16Response

/**
 * UnlockConnector request.
 */
@Serializable
data class UnlockConnectorRequest(
    @SerialName("connectorId") val connectorId: Int
) : Ocpp16Request {
    companion object { const val ACTION = "UnlockConnector" }
}

@Serializable
data class UnlockConnectorResponse(
    @SerialName("status") val status: UnlockStatus
) : Ocpp16Response

/**
 * RemoteStartTransaction request.
 */
@Serializable
data class RemoteStartTransactionRequest(
    @SerialName("idTag") val idTag: String,
    @SerialName("connectorId") val connectorId: Int? = null,
    @SerialName("chargingProfile") val chargingProfile: ChargingProfile? = null
) : Ocpp16Request {
    companion object { const val ACTION = "RemoteStartTransaction" }
}

@Serializable
data class RemoteStartTransactionResponse(
    @SerialName("status") val status: RemoteStartStopStatus
) : Ocpp16Response

/**
 * RemoteStopTransaction request.
 */
@Serializable
data class RemoteStopTransactionRequest(
    @SerialName("transactionId") val transactionId: Int
) : Ocpp16Request {
    companion object { const val ACTION = "RemoteStopTransaction" }
}

@Serializable
data class RemoteStopTransactionResponse(
    @SerialName("status") val status: RemoteStartStopStatus
) : Ocpp16Response

// ============================================================================
// Smart Charging Profile Messages
// ============================================================================

/**
 * SetChargingProfile request.
 */
@Serializable
data class SetChargingProfileRequest(
    @SerialName("connectorId") val connectorId: Int,
    @SerialName("csChargingProfiles") val csChargingProfiles: ChargingProfile
) : Ocpp16Request {
    companion object { const val ACTION = "SetChargingProfile" }
}

@Serializable
data class SetChargingProfileResponse(
    @SerialName("status") val status: ChargingProfileStatus
) : Ocpp16Response

/**
 * ClearChargingProfile request.
 */
@Serializable
data class ClearChargingProfileRequest(
    @SerialName("id") val id: Int? = null,
    @SerialName("connectorId") val connectorId: Int? = null,
    @SerialName("chargingProfilePurpose") val chargingProfilePurpose: ChargingProfilePurposeType? = null,
    @SerialName("stackLevel") val stackLevel: Int? = null
) : Ocpp16Request {
    companion object { const val ACTION = "ClearChargingProfile" }
}

@Serializable
data class ClearChargingProfileResponse(
    @SerialName("status") val status: ClearChargingProfileStatus
) : Ocpp16Response

/**
 * GetCompositeSchedule request.
 */
@Serializable
data class GetCompositeScheduleRequest(
    @SerialName("connectorId") val connectorId: Int,
    @SerialName("duration") val duration: Int,
    @SerialName("chargingRateUnit") val chargingRateUnit: ChargingRateUnitType? = null
) : Ocpp16Request {
    companion object { const val ACTION = "GetCompositeSchedule" }
}

@Serializable
data class GetCompositeScheduleResponse(
    @SerialName("status") val status: GetCompositeScheduleStatus,
    @SerialName("connectorId") val connectorId: Int? = null,
    @SerialName("scheduleStart") val scheduleStart: String? = null,
    @SerialName("chargingSchedule") val chargingSchedule: ChargingSchedule? = null
) : Ocpp16Response

// ============================================================================
// Reservation Profile Messages
// ============================================================================

/**
 * ReserveNow request.
 */
@Serializable
data class ReserveNowRequest(
    @SerialName("connectorId") val connectorId: Int,
    @SerialName("expiryDate") val expiryDate: String,
    @SerialName("idTag") val idTag: String,
    @SerialName("reservationId") val reservationId: Int,
    @SerialName("parentIdTag") val parentIdTag: String? = null
) : Ocpp16Request {
    companion object { const val ACTION = "ReserveNow" }
}

@Serializable
data class ReserveNowResponse(
    @SerialName("status") val status: ReservationStatus
) : Ocpp16Response

/**
 * CancelReservation request.
 */
@Serializable
data class CancelReservationRequest(
    @SerialName("reservationId") val reservationId: Int
) : Ocpp16Request {
    companion object { const val ACTION = "CancelReservation" }
}

@Serializable
data class CancelReservationResponse(
    @SerialName("status") val status: CancelReservationStatus
) : Ocpp16Response

// ============================================================================
// Remote Trigger Profile Messages
// ============================================================================

/**
 * TriggerMessage request.
 */
@Serializable
data class TriggerMessageRequest(
    @SerialName("requestedMessage") val requestedMessage: MessageTrigger,
    @SerialName("connectorId") val connectorId: Int? = null
) : Ocpp16Request {
    companion object { const val ACTION = "TriggerMessage" }
}

@Serializable
data class TriggerMessageResponse(
    @SerialName("status") val status: TriggerMessageStatus
) : Ocpp16Response

// ============================================================================
// Firmware Management Profile Messages
// ============================================================================

/**
 * UpdateFirmware request.
 */
@Serializable
data class UpdateFirmwareRequest(
    @SerialName("location") val location: String,
    @SerialName("retrieveDate") val retrieveDate: String,
    @SerialName("retries") val retries: Int? = null,
    @SerialName("retryInterval") val retryInterval: Int? = null
) : Ocpp16Request {
    companion object { const val ACTION = "UpdateFirmware" }
}

@Serializable
class UpdateFirmwareResponse : Ocpp16Response

/**
 * FirmwareStatusNotification request.
 */
@Serializable
data class FirmwareStatusNotificationRequest(
    @SerialName("status") val status: FirmwareStatus
) : Ocpp16Request {
    companion object { const val ACTION = "FirmwareStatusNotification" }
}

@Serializable
class FirmwareStatusNotificationResponse : Ocpp16Response

/**
 * GetDiagnostics request.
 */
@Serializable
data class GetDiagnosticsRequest(
    @SerialName("location") val location: String,
    @SerialName("retries") val retries: Int? = null,
    @SerialName("retryInterval") val retryInterval: Int? = null,
    @SerialName("startTime") val startTime: String? = null,
    @SerialName("stopTime") val stopTime: String? = null
) : Ocpp16Request {
    companion object { const val ACTION = "GetDiagnostics" }
}

@Serializable
data class GetDiagnosticsResponse(
    @SerialName("fileName") val fileName: String? = null
) : Ocpp16Response

/**
 * DiagnosticsStatusNotification request.
 */
@Serializable
data class DiagnosticsStatusNotificationRequest(
    @SerialName("status") val status: DiagnosticsStatus
) : Ocpp16Request {
    companion object { const val ACTION = "DiagnosticsStatusNotification" }
}

@Serializable
class DiagnosticsStatusNotificationResponse : Ocpp16Response

// ============================================================================
// Configuration Profile Messages
// ============================================================================

/**
 * ChangeConfiguration request.
 */
@Serializable
data class ChangeConfigurationRequest(
    @SerialName("key") val key: String,
    @SerialName("value") val value: String
) : Ocpp16Request {
    companion object { const val ACTION = "ChangeConfiguration" }
}

@Serializable
data class ChangeConfigurationResponse(
    @SerialName("status") val status: ConfigurationStatus
) : Ocpp16Response

/**
 * GetConfiguration request.
 */
@Serializable
data class GetConfigurationRequest(
    @SerialName("key") val key: List<String>? = null
) : Ocpp16Request {
    companion object { const val ACTION = "GetConfiguration" }
}

@Serializable
data class GetConfigurationResponse(
    @SerialName("configurationKey") val configurationKey: List<KeyValue>? = null,
    @SerialName("unknownKey") val unknownKey: List<String>? = null
) : Ocpp16Response

// ============================================================================
// Local Auth List Profile Messages
// ============================================================================

/**
 * GetLocalListVersion request.
 */
@Serializable
class GetLocalListVersionRequest : Ocpp16Request {
    companion object { const val ACTION = "GetLocalListVersion" }
}

@Serializable
data class GetLocalListVersionResponse(
    @SerialName("listVersion") val listVersion: Int
) : Ocpp16Response

/**
 * SendLocalList request.
 */
@Serializable
data class SendLocalListRequest(
    @SerialName("listVersion") val listVersion: Int,
    @SerialName("updateType") val updateType: UpdateType,
    @SerialName("localAuthorizationList") val localAuthorizationList: List<AuthorizationData>? = null
) : Ocpp16Request {
    companion object { const val ACTION = "SendLocalList" }
}

@Serializable
data class SendLocalListResponse(
    @SerialName("status") val status: UpdateStatus
) : Ocpp16Response

/**
 * ClearCache request.
 */
@Serializable
class ClearCacheRequest : Ocpp16Request {
    companion object { const val ACTION = "ClearCache" }
}

@Serializable
data class ClearCacheResponse(
    @SerialName("status") val status: ClearCacheStatus
) : Ocpp16Response

// ============================================================================
// Data Transfer Messages
// ============================================================================

/**
 * DataTransfer request.
 */
@Serializable
data class DataTransferRequest(
    @SerialName("vendorId") val vendorId: String,
    @SerialName("messageId") val messageId: String? = null,
    @SerialName("data") val data: String? = null
) : Ocpp16Request {
    companion object { const val ACTION = "DataTransfer" }
}

@Serializable
data class DataTransferResponse(
    @SerialName("status") val status: DataTransferStatus,
    @SerialName("data") val data: String? = null
) : Ocpp16Response
