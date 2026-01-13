package com.ocpp.v201.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================================
// OCPP 2.0.1 Common Data Types
// ============================================================================

/**
 * Status info type - Contains additional status information.
 */
@Serializable
data class StatusInfoType(
    @SerialName("reasonCode") val reasonCode: String,
    @SerialName("additionalInfo") val additionalInfo: String? = null
)

/**
 * EVSE type - Represents an Electric Vehicle Supply Equipment.
 */
@Serializable
data class EVSEType(
    @SerialName("id") val id: Int,
    @SerialName("connectorId") val connectorId: Int? = null
)

/**
 * Charging station type.
 */
@Serializable
data class ChargingStationType(
    @SerialName("model") val model: String,
    @SerialName("vendorName") val vendorName: String,
    @SerialName("serialNumber") val serialNumber: String? = null,
    @SerialName("firmwareVersion") val firmwareVersion: String? = null,
    @SerialName("modem") val modem: ModemType? = null
)

/**
 * Modem type.
 */
@Serializable
data class ModemType(
    @SerialName("iccid") val iccid: String? = null,
    @SerialName("imsi") val imsi: String? = null
)

/**
 * IdToken type.
 */
@Serializable
data class IdTokenType(
    @SerialName("idToken") val idToken: String,
    @SerialName("type") val type: IdTokenEnumType,
    @SerialName("additionalInfo") val additionalInfo: List<AdditionalInfoType>? = null
)

/**
 * Additional info type.
 */
@Serializable
data class AdditionalInfoType(
    @SerialName("additionalIdToken") val additionalIdToken: String,
    @SerialName("type") val type: String
)

/**
 * IdToken info type.
 */
@Serializable
data class IdTokenInfoType(
    @SerialName("status") val status: AuthorizationStatusEnumType,
    @SerialName("cacheExpiryDateTime") val cacheExpiryDateTime: String? = null,
    @SerialName("chargingPriority") val chargingPriority: Int? = null,
    @SerialName("language1") val language1: String? = null,
    @SerialName("evseId") val evseId: List<Int>? = null,
    @SerialName("groupIdToken") val groupIdToken: IdTokenType? = null,
    @SerialName("language2") val language2: String? = null,
    @SerialName("personalMessage") val personalMessage: MessageContentType? = null
)

/**
 * Message content type.
 */
@Serializable
data class MessageContentType(
    @SerialName("format") val format: MessageFormatEnumType,
    @SerialName("language") val language: String? = null,
    @SerialName("content") val content: String
)

/**
 * Transaction type.
 */
@Serializable
data class TransactionType(
    @SerialName("transactionId") val transactionId: String,
    @SerialName("chargingState") val chargingState: ChargingStateEnumType? = null,
    @SerialName("timeSpentCharging") val timeSpentCharging: Int? = null,
    @SerialName("stoppedReason") val stoppedReason: ReasonEnumType? = null,
    @SerialName("remoteStartId") val remoteStartId: Int? = null
)

/**
 * Meter value type.
 */
@Serializable
data class MeterValueType(
    @SerialName("timestamp") val timestamp: String,
    @SerialName("sampledValue") val sampledValue: List<SampledValueType>
)

/**
 * Sampled value type.
 */
@Serializable
data class SampledValueType(
    @SerialName("value") val value: Double,
    @SerialName("context") val context: ReadingContextEnumType? = null,
    @SerialName("measurand") val measurand: MeasurandEnumType? = null,
    @SerialName("phase") val phase: PhaseEnumType? = null,
    @SerialName("location") val location: LocationEnumType? = null,
    @SerialName("signedMeterValue") val signedMeterValue: SignedMeterValueType? = null,
    @SerialName("unitOfMeasure") val unitOfMeasure: UnitOfMeasureType? = null
)

/**
 * Signed meter value type.
 */
@Serializable
data class SignedMeterValueType(
    @SerialName("signedMeterData") val signedMeterData: String,
    @SerialName("signingMethod") val signingMethod: String,
    @SerialName("encodingMethod") val encodingMethod: String,
    @SerialName("publicKey") val publicKey: String
)

/**
 * Unit of measure type.
 */
@Serializable
data class UnitOfMeasureType(
    @SerialName("unit") val unit: String? = null,
    @SerialName("multiplier") val multiplier: Int? = null
)

/**
 * Charging profile type.
 */
@Serializable
data class ChargingProfileType(
    @SerialName("id") val id: Int,
    @SerialName("stackLevel") val stackLevel: Int,
    @SerialName("chargingProfilePurpose") val chargingProfilePurpose: ChargingProfilePurposeEnumType,
    @SerialName("chargingProfileKind") val chargingProfileKind: ChargingProfileKindEnumType,
    @SerialName("chargingSchedule") val chargingSchedule: List<ChargingScheduleType>,
    @SerialName("recurrencyKind") val recurrencyKind: RecurrencyKindEnumType? = null,
    @SerialName("validFrom") val validFrom: String? = null,
    @SerialName("validTo") val validTo: String? = null,
    @SerialName("transactionId") val transactionId: String? = null
)

/**
 * Charging schedule type.
 */
@Serializable
data class ChargingScheduleType(
    @SerialName("id") val id: Int,
    @SerialName("chargingRateUnit") val chargingRateUnit: ChargingRateUnitEnumType,
    @SerialName("chargingSchedulePeriod") val chargingSchedulePeriod: List<ChargingSchedulePeriodType>,
    @SerialName("startSchedule") val startSchedule: String? = null,
    @SerialName("duration") val duration: Int? = null,
    @SerialName("minChargingRate") val minChargingRate: Double? = null,
    @SerialName("salesTariff") val salesTariff: SalesTariffType? = null
)

/**
 * Charging schedule period type.
 */
@Serializable
data class ChargingSchedulePeriodType(
    @SerialName("startPeriod") val startPeriod: Int,
    @SerialName("limit") val limit: Double,
    @SerialName("numberPhases") val numberPhases: Int? = null,
    @SerialName("phaseToUse") val phaseToUse: Int? = null
)

/**
 * Sales tariff type.
 */
@Serializable
data class SalesTariffType(
    @SerialName("id") val id: Int,
    @SerialName("salesTariffEntry") val salesTariffEntry: List<SalesTariffEntryType>,
    @SerialName("salesTariffDescription") val salesTariffDescription: String? = null,
    @SerialName("numEPriceLevels") val numEPriceLevels: Int? = null
)

/**
 * Sales tariff entry type.
 */
@Serializable
data class SalesTariffEntryType(
    @SerialName("ePriceLevel") val ePriceLevel: Int? = null,
    @SerialName("relativeTimeInterval") val relativeTimeInterval: RelativeTimeIntervalType,
    @SerialName("consumptionCost") val consumptionCost: List<ConsumptionCostType>? = null
)

/**
 * Relative time interval type.
 */
@Serializable
data class RelativeTimeIntervalType(
    @SerialName("start") val start: Int,
    @SerialName("duration") val duration: Int? = null
)

/**
 * Consumption cost type.
 */
@Serializable
data class ConsumptionCostType(
    @SerialName("startValue") val startValue: Double,
    @SerialName("cost") val cost: List<CostType>
)

/**
 * Cost type.
 */
@Serializable
data class CostType(
    @SerialName("costKind") val costKind: CostKindEnumType,
    @SerialName("amount") val amount: Int,
    @SerialName("amountMultiplier") val amountMultiplier: Int? = null
)

/**
 * Cost kind enumeration.
 */
@Serializable
enum class CostKindEnumType {
    @SerialName("CarbonDioxideEmission") CarbonDioxideEmission,
    @SerialName("RelativePricePercentage") RelativePricePercentage,
    @SerialName("RenewableGenerationPercentage") RenewableGenerationPercentage
}

/**
 * Composite schedule type.
 */
@Serializable
data class CompositeScheduleType(
    @SerialName("evseId") val evseId: Int,
    @SerialName("duration") val duration: Int,
    @SerialName("scheduleStart") val scheduleStart: String,
    @SerialName("chargingRateUnit") val chargingRateUnit: ChargingRateUnitEnumType,
    @SerialName("chargingSchedulePeriod") val chargingSchedulePeriod: List<ChargingSchedulePeriodType>
)

/**
 * Component type.
 */
@Serializable
data class ComponentType(
    @SerialName("name") val name: String,
    @SerialName("instance") val instance: String? = null,
    @SerialName("evse") val evse: EVSEType? = null
)

/**
 * Variable type.
 */
@Serializable
data class VariableType(
    @SerialName("name") val name: String,
    @SerialName("instance") val instance: String? = null
)

/**
 * Get variable data type.
 */
@Serializable
data class GetVariableDataType(
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("attributeType") val attributeType: AttributeEnumType? = null
)

/**
 * Get variable result type.
 */
@Serializable
data class GetVariableResultType(
    @SerialName("attributeStatus") val attributeStatus: GetVariableStatusEnumType,
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("attributeType") val attributeType: AttributeEnumType? = null,
    @SerialName("attributeValue") val attributeValue: String? = null,
    @SerialName("attributeStatusInfo") val attributeStatusInfo: StatusInfoType? = null
)

/**
 * Set variable data type.
 */
@Serializable
data class SetVariableDataType(
    @SerialName("attributeValue") val attributeValue: String,
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("attributeType") val attributeType: AttributeEnumType? = null
)

/**
 * Set variable result type.
 */
@Serializable
data class SetVariableResultType(
    @SerialName("attributeStatus") val attributeStatus: SetVariableStatusEnumType,
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("attributeType") val attributeType: AttributeEnumType? = null,
    @SerialName("attributeStatusInfo") val attributeStatusInfo: StatusInfoType? = null
)

/**
 * Firmware type.
 */
@Serializable
data class FirmwareType(
    @SerialName("location") val location: String,
    @SerialName("retrieveDateTime") val retrieveDateTime: String,
    @SerialName("installDateTime") val installDateTime: String? = null,
    @SerialName("signingCertificate") val signingCertificate: String? = null,
    @SerialName("signature") val signature: String? = null
)

/**
 * Log parameters type.
 */
@Serializable
data class LogParametersType(
    @SerialName("remoteLocation") val remoteLocation: String,
    @SerialName("oldestTimestamp") val oldestTimestamp: String? = null,
    @SerialName("latestTimestamp") val latestTimestamp: String? = null
)

/**
 * Certificate hash data type.
 */
@Serializable
data class CertificateHashDataType(
    @SerialName("hashAlgorithm") val hashAlgorithm: HashAlgorithmEnumType,
    @SerialName("issuerNameHash") val issuerNameHash: String,
    @SerialName("issuerKeyHash") val issuerKeyHash: String,
    @SerialName("serialNumber") val serialNumber: String
)

/**
 * OCSP request data type.
 */
@Serializable
data class OCSPRequestDataType(
    @SerialName("hashAlgorithm") val hashAlgorithm: HashAlgorithmEnumType,
    @SerialName("issuerNameHash") val issuerNameHash: String,
    @SerialName("issuerKeyHash") val issuerKeyHash: String,
    @SerialName("serialNumber") val serialNumber: String,
    @SerialName("responderURL") val responderURL: String
)

/**
 * Message info type.
 */
@Serializable
data class MessageInfoType(
    @SerialName("id") val id: Int,
    @SerialName("priority") val priority: MessagePriorityEnumType,
    @SerialName("message") val message: MessageContentType,
    @SerialName("state") val state: MessageStateEnumType? = null,
    @SerialName("startDateTime") val startDateTime: String? = null,
    @SerialName("endDateTime") val endDateTime: String? = null,
    @SerialName("transactionId") val transactionId: String? = null,
    @SerialName("display") val display: ComponentType? = null
)

/**
 * Clear charging profile type.
 */
@Serializable
data class ClearChargingProfileType(
    @SerialName("evseId") val evseId: Int? = null,
    @SerialName("chargingProfilePurpose") val chargingProfilePurpose: ChargingProfilePurposeEnumType? = null,
    @SerialName("stackLevel") val stackLevel: Int? = null
)

/**
 * Charging profile criterion type.
 */
@Serializable
data class ChargingProfileCriterionType(
    @SerialName("chargingProfilePurpose") val chargingProfilePurpose: ChargingProfilePurposeEnumType? = null,
    @SerialName("stackLevel") val stackLevel: Int? = null,
    @SerialName("chargingProfileId") val chargingProfileId: List<Int>? = null,
    @SerialName("chargingLimitSource") val chargingLimitSource: List<ChargingLimitSourceEnumType>? = null
)

/**
 * Charging limit source enumeration.
 */
@Serializable
enum class ChargingLimitSourceEnumType {
    @SerialName("EMS") EMS,
    @SerialName("Other") Other,
    @SerialName("SO") SO,
    @SerialName("CSO") CSO
}

/**
 * Charging limit type.
 */
@Serializable
data class ChargingLimitType(
    @SerialName("chargingLimitSource") val chargingLimitSource: ChargingLimitSourceEnumType,
    @SerialName("isGridCritical") val isGridCritical: Boolean? = null
)

/**
 * Charging needs type.
 */
@Serializable
data class ChargingNeedsType(
    @SerialName("requestedEnergyTransfer") val requestedEnergyTransfer: EnergyTransferModeEnumType,
    @SerialName("departureTime") val departureTime: String? = null,
    @SerialName("acChargingParameters") val acChargingParameters: ACChargingParametersType? = null,
    @SerialName("dcChargingParameters") val dcChargingParameters: DCChargingParametersType? = null
)

/**
 * Energy transfer mode enumeration.
 */
@Serializable
enum class EnergyTransferModeEnumType {
    @SerialName("DC") DC,
    @SerialName("AC_single_phase") ACSinglePhase,
    @SerialName("AC_two_phase") ACTwoPhase,
    @SerialName("AC_three_phase") ACThreePhase
}

/**
 * AC charging parameters type.
 */
@Serializable
data class ACChargingParametersType(
    @SerialName("energyAmount") val energyAmount: Int,
    @SerialName("evMinCurrent") val evMinCurrent: Int,
    @SerialName("evMaxCurrent") val evMaxCurrent: Int,
    @SerialName("evMaxVoltage") val evMaxVoltage: Int
)

/**
 * DC charging parameters type.
 */
@Serializable
data class DCChargingParametersType(
    @SerialName("evMaxCurrent") val evMaxCurrent: Int,
    @SerialName("evMaxVoltage") val evMaxVoltage: Int,
    @SerialName("energyAmount") val energyAmount: Int? = null,
    @SerialName("evMaxPower") val evMaxPower: Int? = null,
    @SerialName("stateOfCharge") val stateOfCharge: Int? = null,
    @SerialName("evEnergyCapacity") val evEnergyCapacity: Int? = null,
    @SerialName("fullSoC") val fullSoC: Int? = null,
    @SerialName("bulkSoC") val bulkSoC: Int? = null
)

/**
 * Event data type.
 */
@Serializable
data class EventDataType(
    @SerialName("eventId") val eventId: Int,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("trigger") val trigger: EventTriggerEnumType,
    @SerialName("actualValue") val actualValue: String,
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("cause") val cause: Int? = null,
    @SerialName("techCode") val techCode: String? = null,
    @SerialName("techInfo") val techInfo: String? = null,
    @SerialName("cleared") val cleared: Boolean? = null,
    @SerialName("transactionId") val transactionId: String? = null,
    @SerialName("variableMonitoringId") val variableMonitoringId: Int? = null,
    @SerialName("eventNotificationType") val eventNotificationType: EventNotificationEnumType
)

/**
 * Event trigger enumeration.
 */
@Serializable
enum class EventTriggerEnumType {
    @SerialName("Alerting") Alerting,
    @SerialName("Delta") Delta,
    @SerialName("Periodic") Periodic
}

/**
 * Event notification enumeration.
 */
@Serializable
enum class EventNotificationEnumType {
    @SerialName("HardWiredNotification") HardWiredNotification,
    @SerialName("HardWiredMonitor") HardWiredMonitor,
    @SerialName("PreconfiguredMonitor") PreconfiguredMonitor,
    @SerialName("CustomMonitor") CustomMonitor
}

/**
 * Set monitoring data type.
 */
@Serializable
data class SetMonitoringDataType(
    @SerialName("value") val value: Double,
    @SerialName("type") val type: MonitorEnumType,
    @SerialName("severity") val severity: Int,
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("id") val id: Int? = null,
    @SerialName("transaction") val transaction: Boolean? = null
)

/**
 * Monitor enumeration.
 */
@Serializable
enum class MonitorEnumType {
    @SerialName("UpperThreshold") UpperThreshold,
    @SerialName("LowerThreshold") LowerThreshold,
    @SerialName("Delta") Delta,
    @SerialName("Periodic") Periodic,
    @SerialName("PeriodicClockAligned") PeriodicClockAligned
}

/**
 * Set monitoring result type.
 */
@Serializable
data class SetMonitoringResultType(
    @SerialName("status") val status: SetMonitoringStatusEnumType,
    @SerialName("type") val type: MonitorEnumType,
    @SerialName("severity") val severity: Int,
    @SerialName("component") val component: ComponentType,
    @SerialName("variable") val variable: VariableType,
    @SerialName("id") val id: Int? = null,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
)

/**
 * Set monitoring status enumeration.
 */
@Serializable
enum class SetMonitoringStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("UnknownComponent") UnknownComponent,
    @SerialName("UnknownVariable") UnknownVariable,
    @SerialName("UnsupportedMonitorType") UnsupportedMonitorType,
    @SerialName("Rejected") Rejected,
    @SerialName("Duplicate") Duplicate
}

/**
 * Clear monitoring result type.
 */
@Serializable
data class ClearMonitoringResultType(
    @SerialName("status") val status: ClearMonitoringStatusEnumType,
    @SerialName("id") val id: Int,
    @SerialName("statusInfo") val statusInfo: StatusInfoType? = null
)

/**
 * Clear monitoring status enumeration.
 */
@Serializable
enum class ClearMonitoringStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("NotFound") NotFound
}

/**
 * Authorization data type for local list.
 */
@Serializable
data class AuthorizationData(
    @SerialName("idToken") val idToken: IdTokenType,
    @SerialName("idTokenInfo") val idTokenInfo: IdTokenInfoType? = null
)

/**
 * Update type enumeration for local list.
 */
@Serializable
enum class UpdateEnumType {
    @SerialName("Differential") Differential,
    @SerialName("Full") Full
}

/**
 * Send local list status enumeration.
 */
@Serializable
enum class SendLocalListStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Failed") Failed,
    @SerialName("VersionMismatch") VersionMismatch
}
