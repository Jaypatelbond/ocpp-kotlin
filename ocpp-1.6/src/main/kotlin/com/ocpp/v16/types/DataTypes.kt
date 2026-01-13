package com.ocpp.v16.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================================
// OCPP 1.6 Data Types
// ============================================================================

/**
 * IdTag info type.
 */
@Serializable
data class IdTagInfo(
    @SerialName("status") val status: AuthorizationStatus,
    @SerialName("expiryDate") val expiryDate: String? = null,
    @SerialName("parentIdTag") val parentIdTag: String? = null
)

/**
 * Meter value type.
 */
@Serializable
data class MeterValue(
    @SerialName("timestamp") val timestamp: String,
    @SerialName("sampledValue") val sampledValue: List<SampledValue>
)

/**
 * Sampled value type.
 */
@Serializable
data class SampledValue(
    @SerialName("value") val value: String,
    @SerialName("context") val context: ReadingContext? = null,
    @SerialName("format") val format: ValueFormat? = null,
    @SerialName("measurand") val measurand: Measurand? = null,
    @SerialName("phase") val phase: Phase? = null,
    @SerialName("location") val location: Location? = null,
    @SerialName("unit") val unit: UnitOfMeasure? = null
)

/**
 * Charging profile type.
 */
@Serializable
data class ChargingProfile(
    @SerialName("chargingProfileId") val chargingProfileId: Int,
    @SerialName("transactionId") val transactionId: Int? = null,
    @SerialName("stackLevel") val stackLevel: Int,
    @SerialName("chargingProfilePurpose") val chargingProfilePurpose: ChargingProfilePurposeType,
    @SerialName("chargingProfileKind") val chargingProfileKind: ChargingProfileKindType,
    @SerialName("recurrencyKind") val recurrencyKind: RecurrencyKindType? = null,
    @SerialName("validFrom") val validFrom: String? = null,
    @SerialName("validTo") val validTo: String? = null,
    @SerialName("chargingSchedule") val chargingSchedule: ChargingSchedule
)

/**
 * Charging schedule type.
 */
@Serializable
data class ChargingSchedule(
    @SerialName("duration") val duration: Int? = null,
    @SerialName("startSchedule") val startSchedule: String? = null,
    @SerialName("chargingRateUnit") val chargingRateUnit: ChargingRateUnitType,
    @SerialName("chargingSchedulePeriod") val chargingSchedulePeriod: List<ChargingSchedulePeriod>,
    @SerialName("minChargingRate") val minChargingRate: Double? = null
)

/**
 * Charging schedule period type.
 */
@Serializable
data class ChargingSchedulePeriod(
    @SerialName("startPeriod") val startPeriod: Int,
    @SerialName("limit") val limit: Double,
    @SerialName("numberPhases") val numberPhases: Int? = null
)

/**
 * Authorization data for local list.
 */
@Serializable
data class AuthorizationData(
    @SerialName("idTag") val idTag: String,
    @SerialName("idTagInfo") val idTagInfo: IdTagInfo? = null
)

/**
 * Key value pair for configuration.
 */
@Serializable
data class KeyValue(
    @SerialName("key") val key: String,
    @SerialName("readonly") val readonly: Boolean,
    @SerialName("value") val value: String? = null
)
