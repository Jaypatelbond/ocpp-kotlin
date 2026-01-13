package com.ocpp.v201.messages

import com.ocpp.v201.types.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================================
// Smart Charging Functional Block Messages
// ============================================================================

/**
 * SetChargingProfile request.
 */
@Serializable
data class SetChargingProfileRequest(
    @SerialName("evseId") val evseId: Int,
    @SerialName("chargingProfile") val chargingProfile: ChargingProfileType
) : Ocpp201Request {
    companion object {
        const val ACTION = "SetChargingProfile"
    }
}

/**
 * SetChargingProfile response.
 */
@Serializable
data class SetChargingProfileResponse(
    @SerialName("status") val status: ChargingProfileStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * GetChargingProfiles request.
 */
@Serializable
data class GetChargingProfilesRequest(
    @SerialName("requestId") val requestId: Int,
    @SerialName("chargingProfile") val chargingProfile: ChargingProfileCriterionType,
    @SerialName("evseId") val evseId: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetChargingProfiles"
    }
}

/**
 * GetChargingProfiles response.
 */
@Serializable
data class GetChargingProfilesResponse(
    @SerialName("status") val status: GetChargingProfileStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Get charging profile status enumeration.
 */
@Serializable
enum class GetChargingProfileStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("NoProfiles") NoProfiles
}

/**
 * ClearChargingProfile request.
 */
@Serializable
data class ClearChargingProfileRequest(
    @SerialName("chargingProfileId") val chargingProfileId: Int? = null,
    @SerialName("chargingProfileCriteria") val chargingProfileCriteria: ClearChargingProfileType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "ClearChargingProfile"
    }
}

/**
 * ClearChargingProfile response.
 */
@Serializable
data class ClearChargingProfileResponse(
    @SerialName("status") val status: ClearChargingProfileStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * ReportChargingProfiles request.
 */
@Serializable
data class ReportChargingProfilesRequest(
    @SerialName("requestId") val requestId: Int,
    @SerialName("chargingLimitSource") val chargingLimitSource: ChargingLimitSourceEnumType,
    @SerialName("evseId") val evseId: Int,
    @SerialName("chargingProfile") val chargingProfile: List<ChargingProfileType>,
    @SerialName("tbc") val tbc: Boolean? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "ReportChargingProfiles"
    }
}

/**
 * ReportChargingProfiles response.
 */
@Serializable
class ReportChargingProfilesResponse : Ocpp201Response

/**
 * GetCompositeSchedule request.
 */
@Serializable
data class GetCompositeScheduleRequest(
    @SerialName("duration") val duration: Int,
    @SerialName("evseId") val evseId: Int,
    @SerialName("chargingRateUnit") val chargingRateUnit: ChargingRateUnitEnumType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetCompositeSchedule"
    }
}

/**
 * GetCompositeSchedule response.
 */
@Serializable
data class GetCompositeScheduleResponse(
    @SerialName("status") val status: GenericStatusEnumType,
    @SerialName("schedule") val schedule: CompositeScheduleType? = null,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Generic status enumeration.
 */
@Serializable
enum class GenericStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * NotifyChargingLimit request.
 */
@Serializable
data class NotifyChargingLimitRequest(
    @SerialName("chargingLimit") val chargingLimit: ChargingLimitType,
    @SerialName("chargingSchedule") val chargingSchedule: List<ChargingScheduleType>? = null,
    @SerialName("evseId") val evseId: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "NotifyChargingLimit"
    }
}

/**
 * NotifyChargingLimit response.
 */
@Serializable
class NotifyChargingLimitResponse : Ocpp201Response

/**
 * NotifyEVChargingNeeds request.
 */
@Serializable
data class NotifyEVChargingNeedsRequest(
    @SerialName("evseId") val evseId: Int,
    @SerialName("chargingNeeds") val chargingNeeds: ChargingNeedsType,
    @SerialName("maxScheduleTuples") val maxScheduleTuples: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "NotifyEVChargingNeeds"
    }
}

/**
 * NotifyEVChargingNeeds response.
 */
@Serializable
data class NotifyEVChargingNeedsResponse(
    @SerialName("status") val status: NotifyEVChargingNeedsStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Notify EV charging needs status enumeration.
 */
@Serializable
enum class NotifyEVChargingNeedsStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("Processing") Processing
}

/**
 * NotifyEVChargingSchedule request.
 */
@Serializable
data class NotifyEVChargingScheduleRequest(
    @SerialName("timeBase") val timeBase: String,
    @SerialName("evseId") val evseId: Int,
    @SerialName("chargingSchedule") val chargingSchedule: ChargingScheduleType
) : Ocpp201Request {
    companion object {
        const val ACTION = "NotifyEVChargingSchedule"
    }
}

/**
 * NotifyEVChargingSchedule response.
 */
@Serializable
data class NotifyEVChargingScheduleResponse(
    @SerialName("status") val status: GenericStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

// ============================================================================
// Firmware Management Functional Block Messages
// ============================================================================

/**
 * UpdateFirmware request.
 */
@Serializable
data class UpdateFirmwareRequest(
    @SerialName("requestId") val requestId: Int,
    @SerialName("firmware") val firmware: FirmwareType,
    @SerialName("retries") val retries: Int? = null,
    @SerialName("retryInterval") val retryInterval: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "UpdateFirmware"
    }
}

/**
 * UpdateFirmware response.
 */
@Serializable
data class UpdateFirmwareResponse(
    @SerialName("status") val status: UpdateFirmwareStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * FirmwareStatusNotification request.
 */
@Serializable
data class FirmwareStatusNotificationRequest(
    @SerialName("status") val status: FirmwareStatusEnumType,
    @SerialName("requestId") val requestId: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "FirmwareStatusNotification"
    }
}

/**
 * FirmwareStatusNotification response.
 */
@Serializable
class FirmwareStatusNotificationResponse : Ocpp201Response

/**
 * PublishFirmware request.
 */
@Serializable
data class PublishFirmwareRequest(
    @SerialName("location") val location: String,
    @SerialName("checksum") val checksum: String,
    @SerialName("requestId") val requestId: Int,
    @SerialName("retries") val retries: Int? = null,
    @SerialName("retryInterval") val retryInterval: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "PublishFirmware"
    }
}

/**
 * PublishFirmware response.
 */
@Serializable
data class PublishFirmwareResponse(
    @SerialName("status") val status: GenericStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * PublishFirmwareStatusNotification request.
 */
@Serializable
data class PublishFirmwareStatusNotificationRequest(
    @SerialName("status") val status: PublishFirmwareStatusEnumType,
    @SerialName("location") val location: List<String>? = null,
    @SerialName("requestId") val requestId: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "PublishFirmwareStatusNotification"
    }
}

/**
 * Publish firmware status enumeration.
 */
@Serializable
enum class PublishFirmwareStatusEnumType {
    @SerialName("Idle") Idle,
    @SerialName("DownloadScheduled") DownloadScheduled,
    @SerialName("Downloading") Downloading,
    @SerialName("Downloaded") Downloaded,
    @SerialName("Published") Published,
    @SerialName("DownloadFailed") DownloadFailed,
    @SerialName("DownloadPaused") DownloadPaused,
    @SerialName("InvalidChecksum") InvalidChecksum,
    @SerialName("ChecksumVerified") ChecksumVerified,
    @SerialName("PublishFailed") PublishFailed
}

/**
 * PublishFirmwareStatusNotification response.
 */
@Serializable
class PublishFirmwareStatusNotificationResponse : Ocpp201Response

/**
 * UnpublishFirmware request.
 */
@Serializable
data class UnpublishFirmwareRequest(
    @SerialName("checksum") val checksum: String
) : Ocpp201Request {
    companion object {
        const val ACTION = "UnpublishFirmware"
    }
}

/**
 * UnpublishFirmware response.
 */
@Serializable
data class UnpublishFirmwareResponse(
    @SerialName("status") val status: UnpublishFirmwareStatusEnumType
) : Ocpp201Response

/**
 * Unpublish firmware status enumeration.
 */
@Serializable
enum class UnpublishFirmwareStatusEnumType {
    @SerialName("DownloadOngoing") DownloadOngoing,
    @SerialName("NoFirmware") NoFirmware,
    @SerialName("Unpublished") Unpublished
}

// ============================================================================
// Diagnostics Functional Block Messages
// ============================================================================

/**
 * GetLog request.
 */
@Serializable
data class GetLogRequest(
    @SerialName("logType") val logType: LogEnumType,
    @SerialName("requestId") val requestId: Int,
    @SerialName("log") val log: LogParametersType,
    @SerialName("retries") val retries: Int? = null,
    @SerialName("retryInterval") val retryInterval: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetLog"
    }
}

/**
 * GetLog response.
 */
@Serializable
data class GetLogResponse(
    @SerialName("status") val status: LogStatusEnumType,
    @SerialName("filename") val filename: String? = null,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * LogStatusNotification request.
 */
@Serializable
data class LogStatusNotificationRequest(
    @SerialName("status") val status: UploadLogStatusEnumType,
    @SerialName("requestId") val requestId: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "LogStatusNotification"
    }
}

/**
 * LogStatusNotification response.
 */
@Serializable
class LogStatusNotificationResponse : Ocpp201Response

/**
 * SetVariableMonitoring request.
 */
@Serializable
data class SetVariableMonitoringRequest(
    @SerialName("setMonitoringData") val setMonitoringData: List<SetMonitoringDataType>
) : Ocpp201Request {
    companion object {
        const val ACTION = "SetVariableMonitoring"
    }
}

/**
 * SetVariableMonitoring response.
 */
@Serializable
data class SetVariableMonitoringResponse(
    @SerialName("setMonitoringResult") val setMonitoringResult: List<SetMonitoringResultType>
) : Ocpp201Response

/**
 * ClearVariableMonitoring request.
 */
@Serializable
data class ClearVariableMonitoringRequest(
    @SerialName("id") val id: List<Int>
) : Ocpp201Request {
    companion object {
        const val ACTION = "ClearVariableMonitoring"
    }
}

/**
 * ClearVariableMonitoring response.
 */
@Serializable
data class ClearVariableMonitoringResponse(
    @SerialName("clearMonitoringResult") val clearMonitoringResult: List<ClearMonitoringResultType>
) : Ocpp201Response

/**
 * SetMonitoringBase request.
 */
@Serializable
data class SetMonitoringBaseRequest(
    @SerialName("monitoringBase") val monitoringBase: MonitoringBaseEnumType
) : Ocpp201Request {
    companion object {
        const val ACTION = "SetMonitoringBase"
    }
}

/**
 * Monitoring base enumeration.
 */
@Serializable
enum class MonitoringBaseEnumType {
    @SerialName("All") All,
    @SerialName("FactoryDefault") FactoryDefault,
    @SerialName("HardWiredOnly") HardWiredOnly
}

/**
 * SetMonitoringBase response.
 */
@Serializable
data class SetMonitoringBaseResponse(
    @SerialName("status") val status: GenericDeviceModelStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * SetMonitoringLevel request.
 */
@Serializable
data class SetMonitoringLevelRequest(
    @SerialName("severity") val severity: Int
) : Ocpp201Request {
    companion object {
        const val ACTION = "SetMonitoringLevel"
    }
}

/**
 * SetMonitoringLevel response.
 */
@Serializable
data class SetMonitoringLevelResponse(
    @SerialName("status") val status: GenericStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * NotifyEvent request.
 */
@Serializable
data class NotifyEventRequest(
    @SerialName("generatedAt") val generatedAt: String,
    @SerialName("seqNo") val seqNo: Int,
    @SerialName("eventData") val eventData: List<EventDataType>,
    @SerialName("tbc") val tbc: Boolean? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "NotifyEvent"
    }
}

/**
 * NotifyEvent response.
 */
@Serializable
class NotifyEventResponse : Ocpp201Response

/**
 * NotifyMonitoringReport request.
 */
@Serializable
data class NotifyMonitoringReportRequest(
    @SerialName("requestId") val requestId: Int,
    @SerialName("seqNo") val seqNo: Int,
    @SerialName("generatedAt") val generatedAt: String,
    @SerialName("monitor") val monitor: List<MonitoringDataType>? = null,
    @SerialName("tbc") val tbc: Boolean? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "NotifyMonitoringReport"
    }
}

/**
 * Monitoring data type.
 */
@Serializable
data class MonitoringDataType(
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("variableMonitoring") val variableMonitoring: List<VariableMonitoringType>
)

/**
 * Variable monitoring type.
 */
@Serializable
data class VariableMonitoringType(
    @SerialName("id") val id: Int,
    @SerialName("transaction") val transaction: Boolean,
    @SerialName("value") val value: Double,
    @SerialName("type") val type: MonitorEnumType,
    @SerialName("severity") val severity: Int
)

/**
 * NotifyMonitoringReport response.
 */
@Serializable
class NotifyMonitoringReportResponse : Ocpp201Response

// ============================================================================
// Security Functional Block Messages
// ============================================================================

/**
 * SecurityEventNotification request.
 */
@Serializable
data class SecurityEventNotificationRequest(
    @SerialName("type") val type: String,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("techInfo") val techInfo: String? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "SecurityEventNotification"
    }
}

/**
 * SecurityEventNotification response.
 */
@Serializable
class SecurityEventNotificationResponse : Ocpp201Response

/**
 * SignCertificate request.
 */
@Serializable
data class SignCertificateRequest(
    @SerialName("csr") val csr: String,
    @SerialName("certificateType") val certificateType: CertificateSigningUseEnumType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "SignCertificate"
    }
}

/**
 * SignCertificate response.
 */
@Serializable
data class SignCertificateResponse(
    @SerialName("status") val status: GenericStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * CertificateSigned request.
 */
@Serializable
data class CertificateSignedRequest(
    @SerialName("certificateChain") val certificateChain: String,
    @SerialName("certificateType") val certificateType: CertificateSigningUseEnumType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "CertificateSigned"
    }
}

/**
 * CertificateSigned response.
 */
@Serializable
data class CertificateSignedResponse(
    @SerialName("status") val status: CertificateSignedStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Certificate signed status enumeration.
 */
@Serializable
enum class CertificateSignedStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * GetInstalledCertificateIds request.
 */
@Serializable
data class GetInstalledCertificateIdsRequest(
    @SerialName("certificateType") val certificateType: List<GetCertificateIdUseEnumType>? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetInstalledCertificateIds"
    }
}

/**
 * Get certificate ID use enumeration.
 */
@Serializable
enum class GetCertificateIdUseEnumType {
    @SerialName("V2GRootCertificate") V2GRootCertificate,
    @SerialName("MORootCertificate") MORootCertificate,
    @SerialName("CSMSRootCertificate") CSMSRootCertificate,
    @SerialName("V2GCertificateChain") V2GCertificateChain,
    @SerialName("ManufacturerRootCertificate") ManufacturerRootCertificate
}

/**
 * GetInstalledCertificateIds response.
 */
@Serializable
data class GetInstalledCertificateIdsResponse(
    @SerialName("status") val status: GetInstalledCertificateStatusEnumType,
    @SerialName("certificateHashDataChain") val certificateHashDataChain: List<CertificateHashDataChainType>? = null,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Get installed certificate status enumeration.
 */
@Serializable
enum class GetInstalledCertificateStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("NotFound") NotFound
}

/**
 * Certificate hash data chain type.
 */
@Serializable
data class CertificateHashDataChainType(
    @SerialName("certificateType") val certificateType: GetCertificateIdUseEnumType,
    @SerialName("certificateHashData") val certificateHashData: CertificateHashDataType,
    @SerialName("childCertificateHashData") val childCertificateHashData: List<CertificateHashDataType>? = null
)

/**
 * InstallCertificate request.
 */
@Serializable
data class InstallCertificateRequest(
    @SerialName("certificateType") val certificateType: InstallCertificateUseEnumType,
    @SerialName("certificate") val certificate: String
) : Ocpp201Request {
    companion object {
        const val ACTION = "InstallCertificate"
    }
}

/**
 * InstallCertificate response.
 */
@Serializable
data class InstallCertificateResponse(
    @SerialName("status") val status: InstallCertificateStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * DeleteCertificate request.
 */
@Serializable
data class DeleteCertificateRequest(
    @SerialName("certificateHashData") val certificateHashData: CertificateHashDataType
) : Ocpp201Request {
    companion object {
        const val ACTION = "DeleteCertificate"
    }
}

/**
 * DeleteCertificate response.
 */
@Serializable
data class DeleteCertificateResponse(
    @SerialName("status") val status: DeleteCertificateStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

// ============================================================================
// ISO 15118 Certificate Management Messages
// ============================================================================

/**
 * Get15118EVCertificate request.
 */
@Serializable
data class Get15118EVCertificateRequest(
    @SerialName("iso15118SchemaVersion") val iso15118SchemaVersion: String,
    @SerialName("action") val action: CertificateActionEnumType,
    @SerialName("exiRequest") val exiRequest: String
) : Ocpp201Request {
    companion object {
        const val ACTION = "Get15118EVCertificate"
    }
}

/**
 * Certificate action enumeration.
 */
@Serializable
enum class CertificateActionEnumType {
    @SerialName("Install") Install,
    @SerialName("Update") Update
}

/**
 * Get15118EVCertificate response.
 */
@Serializable
data class Get15118EVCertificateResponse(
    @SerialName("status") val status: Iso15118EVCertificateStatusEnumType,
    @SerialName("exiResponse") val exiResponse: String,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * ISO 15118 EV certificate status enumeration.
 */
@Serializable
enum class Iso15118EVCertificateStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Failed") Failed
}

/**
 * GetCertificateStatus request.
 */
@Serializable
data class GetCertificateStatusRequest(
    @SerialName("ocspRequestData") val ocspRequestData: OCSPRequestDataType
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetCertificateStatus"
    }
}

/**
 * GetCertificateStatus response.
 */
@Serializable
data class GetCertificateStatusResponse(
    @SerialName("status") val status: GetCertificateStatusEnumType,
    @SerialName("ocspResult") val ocspResult: String? = null,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Get certificate status enumeration.
 */
@Serializable
enum class GetCertificateStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Failed") Failed
}

// ============================================================================
// Display Message Functional Block Messages
// ============================================================================

/**
 * SetDisplayMessage request.
 */
@Serializable
data class SetDisplayMessageRequest(
    @SerialName("message") val message: MessageInfoType
) : Ocpp201Request {
    companion object {
        const val ACTION = "SetDisplayMessage"
    }
}

/**
 * SetDisplayMessage response.
 */
@Serializable
data class SetDisplayMessageResponse(
    @SerialName("status") val status: DisplayMessageStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * GetDisplayMessages request.
 */
@Serializable
data class GetDisplayMessagesRequest(
    @SerialName("requestId") val requestId: Int,
    @SerialName("id") val id: List<Int>? = null,
    @SerialName("priority") val priority: MessagePriorityEnumType? = null,
    @SerialName("state") val state: MessageStateEnumType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetDisplayMessages"
    }
}

/**
 * GetDisplayMessages response.
 */
@Serializable
data class GetDisplayMessagesResponse(
    @SerialName("status") val status: GetDisplayMessagesStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Get display messages status enumeration.
 */
@Serializable
enum class GetDisplayMessagesStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Unknown") Unknown
}

/**
 * NotifyDisplayMessages request.
 */
@Serializable
data class NotifyDisplayMessagesRequest(
    @SerialName("requestId") val requestId: Int,
    @SerialName("messageInfo") val messageInfo: List<MessageInfoType>? = null,
    @SerialName("tbc") val tbc: Boolean? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "NotifyDisplayMessages"
    }
}

/**
 * NotifyDisplayMessages response.
 */
@Serializable
class NotifyDisplayMessagesResponse : Ocpp201Response

/**
 * ClearDisplayMessage request.
 */
@Serializable
data class ClearDisplayMessageRequest(
    @SerialName("id") val id: Int
) : Ocpp201Request {
    companion object {
        const val ACTION = "ClearDisplayMessage"
    }
}

/**
 * ClearDisplayMessage response.
 */
@Serializable
data class ClearDisplayMessageResponse(
    @SerialName("status") val status: ClearMessageStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

// ============================================================================
// Tariff and Cost Functional Block Messages
// ============================================================================

/**
 * CostUpdated request.
 */
@Serializable
data class CostUpdatedRequest(
    @SerialName("totalCost") val totalCost: Double,
    @SerialName("transactionId") val transactionId: String
) : Ocpp201Request {
    companion object {
        const val ACTION = "CostUpdated"
    }
}

/**
 * CostUpdated response.
 */
@Serializable
class CostUpdatedResponse : Ocpp201Response

// ============================================================================
// Data Transfer Functional Block Messages
// ============================================================================

/**
 * DataTransfer request - For custom vendor-specific data.
 */
@Serializable
data class DataTransferRequest(
    @SerialName("vendorId") val vendorId: String,
    @SerialName("messageId") val messageId: String? = null,
    @SerialName("data") val data: String? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "DataTransfer"
    }
}

/**
 * DataTransfer response.
 */
@Serializable
data class DataTransferResponse(
    @SerialName("status") val status: DataTransferStatusEnumType,
    @SerialName("data") val data: String? = null,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response
