package com.ocpp.v201.messages

import com.ocpp.v201.types.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================================
// Base interfaces for OCPP requests and responses
// ============================================================================

/**
 * Marker interface for all OCPP 2.0.1 request messages.
 */
interface Ocpp201Request

/**
 * Marker interface for all OCPP 2.0.1 response messages.
 */
interface Ocpp201Response

// ============================================================================
// Provisioning Functional Block Messages
// ============================================================================

/**
 * BootNotification request - Sent by charging station after boot.
 */
@Serializable
data class BootNotificationRequest(
    @SerialName("chargingStation") val chargingStation: ChargingStationType,
    @SerialName("reason") val reason: BootReasonEnumType
) : Ocpp201Request {
    companion object {
        const val ACTION = "BootNotification"
    }
}

/**
 * BootNotification response.
 */
@Serializable
data class BootNotificationResponse(
    @SerialName("currentTime") val currentTime: String,
    @SerialName("interval") val interval: Int,
    @SerialName("status") val status: RegistrationStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Heartbeat request - Keep-alive message.
 */
@Serializable
class HeartbeatRequest : Ocpp201Request {
    companion object {
        const val ACTION = "Heartbeat"
    }
}

/**
 * Heartbeat response.
 */
@Serializable
data class HeartbeatResponse(
    @SerialName("currentTime") val currentTime: String
) : Ocpp201Response

/**
 * GetVariables request - Get configuration variables.
 */
@Serializable
data class GetVariablesRequest(
    @SerialName("getVariableData") val getVariableData: List<GetVariableDataType>
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetVariables"
    }
}

/**
 * GetVariables response.
 */
@Serializable
data class GetVariablesResponse(
    @SerialName("getVariableResult") val getVariableResult: List<GetVariableResultType>
) : Ocpp201Response

/**
 * SetVariables request - Set configuration variables.
 */
@Serializable
data class SetVariablesRequest(
    @SerialName("setVariableData") val setVariableData: List<SetVariableDataType>
) : Ocpp201Request {
    companion object {
        const val ACTION = "SetVariables"
    }
}

/**
 * SetVariables response.
 */
@Serializable
data class SetVariablesResponse(
    @SerialName("setVariableResult") val setVariableResult: List<SetVariableResultType>
) : Ocpp201Response

/**
 * GetBaseReport request.
 */
@Serializable
data class GetBaseReportRequest(
    @SerialName("requestId") val requestId: Int,
    @SerialName("reportBase") val reportBase: ReportBaseEnumType
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetBaseReport"
    }
}

/**
 * GetBaseReport response.
 */
@Serializable
data class GetBaseReportResponse(
    @SerialName("status") val status: GenericDeviceModelStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * NotifyReport request - Report data from charging station.
 */
@Serializable
data class NotifyReportRequest(
    @SerialName("requestId") val requestId: Int,
    @SerialName("generatedAt") val generatedAt: String,
    @SerialName("seqNo") val seqNo: Int,
    @SerialName("tbc") val tbc: Boolean? = null,
    @SerialName("reportData") val reportData: List<ReportDataType>? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "NotifyReport"
    }
}

/**
 * Report data type.
 */
@Serializable
data class ReportDataType(
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("variableAttribute") val variableAttribute: List<VariableAttributeType>,
    @SerialName("variableCharacteristics") val variableCharacteristics: VariableCharacteristicsType? = null
)

/**
 * Variable attribute type.
 */
@Serializable
data class VariableAttributeType(
    @SerialName("type") val type: AttributeEnumType? = null,
    @SerialName("value") val value: String? = null,
    @SerialName("mutability") val mutability: MutabilityEnumType? = null,
    @SerialName("persistent") val persistent: Boolean? = null,
    @SerialName("constant") val constant: Boolean? = null
)

/**
 * Mutability enumeration.
 */
@Serializable
enum class MutabilityEnumType {
    @SerialName("ReadOnly") ReadOnly,
    @SerialName("WriteOnly") WriteOnly,
    @SerialName("ReadWrite") ReadWrite
}

/**
 * Variable characteristics type.
 */
@Serializable
data class VariableCharacteristicsType(
    @SerialName("dataType") val dataType: DataEnumType,
    @SerialName("supportsMonitoring") val supportsMonitoring: Boolean,
    @SerialName("unit") val unit: String? = null,
    @SerialName("minLimit") val minLimit: Double? = null,
    @SerialName("maxLimit") val maxLimit: Double? = null,
    @SerialName("valuesList") val valuesList: String? = null
)

/**
 * Data enumeration.
 */
@Serializable
enum class DataEnumType {
    @SerialName("string") STRING,
    @SerialName("decimal") DECIMAL,
    @SerialName("integer") INTEGER,
    @SerialName("dateTime") DATE_TIME,
    @SerialName("boolean") BOOLEAN,
    @SerialName("OptionList") OPTION_LIST,
    @SerialName("SequenceList") SEQUENCE_LIST,
    @SerialName("MemberList") MEMBER_LIST
}

/**
 * NotifyReport response.
 */
@Serializable
class NotifyReportResponse : Ocpp201Response

/**
 * Reset request.
 */
@Serializable
data class ResetRequest(
    @SerialName("type") val type: ResetEnumType,
    @SerialName("evseId") val evseId: Int? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "Reset"
    }
}

/**
 * Reset response.
 */
@Serializable
data class ResetResponse(
    @SerialName("status") val status: ResetStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

// ============================================================================
// Authorization Functional Block Messages
// ============================================================================

/**
 * Authorize request.
 */
@Serializable
data class AuthorizeRequest(
    @SerialName("idToken") val idToken: IdTokenType,
    @SerialName("certificate") val certificate: String? = null,
    @SerialName("iso15118CertificateHashData") val iso15118CertificateHashData: List<OCSPRequestDataType>? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "Authorize"
    }
}

/**
 * Authorize response.
 */
@Serializable
data class AuthorizeResponse(
    @SerialName("idTokenInfo") val idTokenInfo: IdTokenInfoType,
    @SerialName("certificateStatus") val certificateStatus: AuthorizeCertificateStatusEnumType? = null
) : Ocpp201Response

/**
 * ClearCache request.
 */
@Serializable
class ClearCacheRequest : Ocpp201Request {
    companion object {
        const val ACTION = "ClearCache"
    }
}

/**
 * ClearCache response.
 */
@Serializable
data class ClearCacheResponse(
    @SerialName("status") val status: ClearCacheStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * Clear cache status enumeration.
 */
@Serializable
enum class ClearCacheStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * GetLocalListVersion request.
 */
@Serializable
class GetLocalListVersionRequest : Ocpp201Request {
    companion object {
        const val ACTION = "GetLocalListVersion"
    }
}

/**
 * GetLocalListVersion response.
 */
@Serializable
data class GetLocalListVersionResponse(
    @SerialName("versionNumber") val versionNumber: Int
) : Ocpp201Response

/**
 * SendLocalList request.
 */
@Serializable
data class SendLocalListRequest(
    @SerialName("versionNumber") val versionNumber: Int,
    @SerialName("updateType") val updateType: UpdateEnumType,
    @SerialName("localAuthorizationList") val localAuthorizationList: List<AuthorizationData>? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "SendLocalList"
    }
}

/**
 * SendLocalList response.
 */
@Serializable
data class SendLocalListResponse(
    @SerialName("status") val status: SendLocalListStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

// ============================================================================
// Transaction Functional Block Messages
// ============================================================================

/**
 * TransactionEvent request.
 */
@Serializable
data class TransactionEventRequest(
    @SerialName("eventType") val eventType: TransactionEventEnumType,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("triggerReason") val triggerReason: TriggerReasonEnumType,
    @SerialName("seqNo") val seqNo: Int,
    @SerialName("transactionInfo") val transactionInfo: TransactionType,
    @SerialName("offline") val offline: Boolean? = null,
    @SerialName("numberOfPhasesUsed") val numberOfPhasesUsed: Int? = null,
    @SerialName("cableMaxCurrent") val cableMaxCurrent: Int? = null,
    @SerialName("reservationId") val reservationId: Int? = null,
    @SerialName("evse") val evse: EVSEType? = null,
    @SerialName("idToken") val idToken: IdTokenType? = null,
    @SerialName("meterValue") val meterValue: List<MeterValueType>? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "TransactionEvent"
    }
}

/**
 * TransactionEvent response.
 */
@Serializable
data class TransactionEventResponse(
    @SerialName("totalCost") val totalCost: Double? = null,
    @SerialName("chargingPriority") val chargingPriority: Int? = null,
    @SerialName("idTokenInfo") val idTokenInfo: IdTokenInfoType? = null,
    @SerialName("updatedPersonalMessage") val updatedPersonalMessage: MessageContentType? = null
) : Ocpp201Response

/**
 * RequestStartTransaction request.
 */
@Serializable
data class RequestStartTransactionRequest(
    @SerialName("idToken") val idToken: IdTokenType,
    @SerialName("remoteStartId") val remoteStartId: Int,
    @SerialName("evseId") val evseId: Int? = null,
    @SerialName("groupIdToken") val groupIdToken: IdTokenType? = null,
    @SerialName("chargingProfile") val chargingProfile: ChargingProfileType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "RequestStartTransaction"
    }
}

/**
 * RequestStartTransaction response.
 */
@Serializable
data class RequestStartTransactionResponse(
    @SerialName("status") val status: RequestStartStopStatusEnumType,
    @SerialName("transactionId") val transactionId: String? = null,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * RequestStopTransaction request.
 */
@Serializable
data class RequestStopTransactionRequest(
    @SerialName("transactionId") val transactionId: String
) : Ocpp201Request {
    companion object {
        const val ACTION = "RequestStopTransaction"
    }
}

/**
 * RequestStopTransaction response.
 */
@Serializable
data class RequestStopTransactionResponse(
    @SerialName("status") val status: RequestStartStopStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * GetTransactionStatus request.
 */
@Serializable
data class GetTransactionStatusRequest(
    @SerialName("transactionId") val transactionId: String? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "GetTransactionStatus"
    }
}

/**
 * GetTransactionStatus response.
 */
@Serializable
data class GetTransactionStatusResponse(
    @SerialName("ongoingIndicator") val ongoingIndicator: Boolean? = null,
    @SerialName("messagesInQueue") val messagesInQueue: Boolean
) : Ocpp201Response

// ============================================================================
// Availability Functional Block Messages
// ============================================================================

/**
 * StatusNotification request.
 */
@Serializable
data class StatusNotificationRequest(
    @SerialName("timestamp") val timestamp: String,
    @SerialName("connectorStatus") val connectorStatus: ConnectorStatusEnumType,
    @SerialName("evseId") val evseId: Int,
    @SerialName("connectorId") val connectorId: Int
) : Ocpp201Request {
    companion object {
        const val ACTION = "StatusNotification"
    }
}

/**
 * StatusNotification response.
 */
@Serializable
class StatusNotificationResponse : Ocpp201Response

/**
 * ChangeAvailability request.
 */
@Serializable
data class ChangeAvailabilityRequest(
    @SerialName("operationalStatus") val operationalStatus: OperationalStatusEnumType,
    @SerialName("evse") val evse: EVSEType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "ChangeAvailability"
    }
}

/**
 * ChangeAvailability response.
 */
@Serializable
data class ChangeAvailabilityResponse(
    @SerialName("status") val status: ChangeAvailabilityStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

// ============================================================================
// Metering Functional Block Messages
// ============================================================================

/**
 * MeterValues request.
 */
@Serializable
data class MeterValuesRequest(
    @SerialName("evseId") val evseId: Int,
    @SerialName("meterValue") val meterValue: List<MeterValueType>
) : Ocpp201Request {
    companion object {
        const val ACTION = "MeterValues"
    }
}

/**
 * MeterValues response.
 */
@Serializable
class MeterValuesResponse : Ocpp201Response

// ============================================================================
// Reservation Functional Block Messages
// ============================================================================

/**
 * ReserveNow request.
 */
@Serializable
data class ReserveNowRequest(
    @SerialName("id") val id: Int,
    @SerialName("expiryDateTime") val expiryDateTime: String,
    @SerialName("idToken") val idToken: IdTokenType,
    @SerialName("connectorType") val connectorType: ConnectorEnumType? = null,
    @SerialName("evseId") val evseId: Int? = null,
    @SerialName("groupIdToken") val groupIdToken: IdTokenType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "ReserveNow"
    }
}

/**
 * Connector enumeration.
 */
@Serializable
enum class ConnectorEnumType {
    @SerialName("cCCS1") CCCS1,
    @SerialName("cCCS2") CCCS2,
    @SerialName("cChaoJi") CChaoJi,
    @SerialName("cG105") CG105,
    @SerialName("cTesla") CTesla,
    @SerialName("cType1") CType1,
    @SerialName("cType2") CType2,
    @SerialName("s309-1P-16A") S309_1P_16A,
    @SerialName("s309-1P-32A") S309_1P_32A,
    @SerialName("s309-3P-16A") S309_3P_16A,
    @SerialName("s309-3P-32A") S309_3P_32A,
    @SerialName("sBS1361") SBS1361,
    @SerialName("sCEE-7-7") SCEE_7_7,
    @SerialName("sType2") SType2,
    @SerialName("sType3") SType3,
    @SerialName("Other1PhMax16A") Other1PhMax16A,
    @SerialName("Other1PhOver16A") Other1PhOver16A,
    @SerialName("Other3Ph") Other3Ph,
    @SerialName("Pan") Pan,
    @SerialName("wInductive") WInductive,
    @SerialName("wResonant") WResonant,
    @SerialName("Undetermined") Undetermined,
    @SerialName("Unknown") Unknown
}

/**
 * ReserveNow response.
 */
@Serializable
data class ReserveNowResponse(
    @SerialName("status") val status: ReserveNowStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * CancelReservation request.
 */
@Serializable
data class CancelReservationRequest(
    @SerialName("reservationId") val reservationId: Int
) : Ocpp201Request {
    companion object {
        const val ACTION = "CancelReservation"
    }
}

/**
 * CancelReservation response.
 */
@Serializable
data class CancelReservationResponse(
    @SerialName("status") val status: CancelReservationStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * ReservationStatusUpdate request.
 */
@Serializable
data class ReservationStatusUpdateRequest(
    @SerialName("reservationId") val reservationId: Int,
    @SerialName("reservationUpdateStatus") val reservationUpdateStatus: ReservationUpdateStatusEnumType
) : Ocpp201Request {
    companion object {
        const val ACTION = "ReservationStatusUpdate"
    }
}

/**
 * Reservation update status enumeration.
 */
@Serializable
enum class ReservationUpdateStatusEnumType {
    @SerialName("Expired") Expired,
    @SerialName("Removed") Removed
}

/**
 * ReservationStatusUpdate response.
 */
@Serializable
class ReservationStatusUpdateResponse : Ocpp201Response

// ============================================================================
// Remote Control Functional Block Messages
// ============================================================================

/**
 * TriggerMessage request.
 */
@Serializable
data class TriggerMessageRequest(
    @SerialName("requestedMessage") val requestedMessage: MessageTriggerEnumType,
    @SerialName("evse") val evse: EVSEType? = null
) : Ocpp201Request {
    companion object {
        const val ACTION = "TriggerMessage"
    }
}

/**
 * TriggerMessage response.
 */
@Serializable
data class TriggerMessageResponse(
    @SerialName("status") val status: TriggerMessageStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response

/**
 * UnlockConnector request.
 */
@Serializable
data class UnlockConnectorRequest(
    @SerialName("evseId") val evseId: Int,
    @SerialName("connectorId") val connectorId: Int
) : Ocpp201Request {
    companion object {
        const val ACTION = "UnlockConnector"
    }
}

/**
 * UnlockConnector response.
 */
@Serializable
data class UnlockConnectorResponse(
    @SerialName("status") val status: UnlockStatusEnumType,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
) : Ocpp201Response
