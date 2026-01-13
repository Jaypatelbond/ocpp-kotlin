package com.ocpp.v16.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================================
// OCPP 1.6 Enumerations
// ============================================================================

/**
 * Registration status - Response to BootNotification.
 */
@Serializable
enum class RegistrationStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Pending") Pending,
    @SerialName("Rejected") Rejected
}

/**
 * Authorization status enumeration.
 */
@Serializable
enum class AuthorizationStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Blocked") Blocked,
    @SerialName("Expired") Expired,
    @SerialName("Invalid") Invalid,
    @SerialName("ConcurrentTx") ConcurrentTx
}

/**
 * Charge point status enumeration.
 */
@Serializable
enum class ChargePointStatus {
    @SerialName("Available") Available,
    @SerialName("Preparing") Preparing,
    @SerialName("Charging") Charging,
    @SerialName("SuspendedEVSE") SuspendedEVSE,
    @SerialName("SuspendedEV") SuspendedEV,
    @SerialName("Finishing") Finishing,
    @SerialName("Reserved") Reserved,
    @SerialName("Unavailable") Unavailable,
    @SerialName("Faulted") Faulted
}

/**
 * Charge point error code enumeration.
 */
@Serializable
enum class ChargePointErrorCode {
    @SerialName("ConnectorLockFailure") ConnectorLockFailure,
    @SerialName("EVCommunicationError") EVCommunicationError,
    @SerialName("GroundFailure") GroundFailure,
    @SerialName("HighTemperature") HighTemperature,
    @SerialName("InternalError") InternalError,
    @SerialName("LocalListConflict") LocalListConflict,
    @SerialName("NoError") NoError,
    @SerialName("OtherError") OtherError,
    @SerialName("OverCurrentFailure") OverCurrentFailure,
    @SerialName("OverVoltage") OverVoltage,
    @SerialName("PowerMeterFailure") PowerMeterFailure,
    @SerialName("PowerSwitchFailure") PowerSwitchFailure,
    @SerialName("ReaderFailure") ReaderFailure,
    @SerialName("ResetFailure") ResetFailure,
    @SerialName("UnderVoltage") UnderVoltage,
    @SerialName("WeakSignal") WeakSignal
}

/**
 * Reason for stopping a transaction.
 */
@Serializable
enum class Reason {
    @SerialName("DeAuthorized") DeAuthorized,
    @SerialName("EmergencyStop") EmergencyStop,
    @SerialName("EVDisconnected") EVDisconnected,
    @SerialName("HardReset") HardReset,
    @SerialName("Local") Local,
    @SerialName("Other") Other,
    @SerialName("PowerLoss") PowerLoss,
    @SerialName("Reboot") Reboot,
    @SerialName("Remote") Remote,
    @SerialName("SoftReset") SoftReset,
    @SerialName("UnlockCommand") UnlockCommand
}

/**
 * Reset type enumeration.
 */
@Serializable
enum class ResetType {
    @SerialName("Hard") Hard,
    @SerialName("Soft") Soft
}

/**
 * Reset status enumeration.
 */
@Serializable
enum class ResetStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * Availability type enumeration.
 */
@Serializable
enum class AvailabilityType {
    @SerialName("Inoperative") Inoperative,
    @SerialName("Operative") Operative
}

/**
 * Availability status enumeration.
 */
@Serializable
enum class AvailabilityStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("Scheduled") Scheduled
}

/**
 * Unlock status enumeration.
 */
@Serializable
enum class UnlockStatus {
    @SerialName("Unlocked") Unlocked,
    @SerialName("UnlockFailed") UnlockFailed,
    @SerialName("NotSupported") NotSupported
}

/**
 * Remote start stop status enumeration.
 */
@Serializable
enum class RemoteStartStopStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * Charging profile purpose enumeration.
 */
@Serializable
enum class ChargingProfilePurposeType {
    @SerialName("ChargePointMaxProfile") ChargePointMaxProfile,
    @SerialName("TxDefaultProfile") TxDefaultProfile,
    @SerialName("TxProfile") TxProfile
}

/**
 * Charging profile kind enumeration.
 */
@Serializable
enum class ChargingProfileKindType {
    @SerialName("Absolute") Absolute,
    @SerialName("Recurring") Recurring,
    @SerialName("Relative") Relative
}

/**
 * Recurrency kind enumeration.
 */
@Serializable
enum class RecurrencyKindType {
    @SerialName("Daily") Daily,
    @SerialName("Weekly") Weekly
}

/**
 * Charging profile status enumeration.
 */
@Serializable
enum class ChargingProfileStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("NotSupported") NotSupported
}

/**
 * Clear charging profile status enumeration.
 */
@Serializable
enum class ClearChargingProfileStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Unknown") Unknown
}

/**
 * Charging rate unit enumeration.
 */
@Serializable
enum class ChargingRateUnitType {
    @SerialName("W") W,
    @SerialName("A") A
}

/**
 * Get composite schedule status enumeration.
 */
@Serializable
enum class GetCompositeScheduleStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * Measurand enumeration.
 */
@Serializable
enum class Measurand {
    @SerialName("Energy.Active.Export.Register") EnergyActiveExportRegister,
    @SerialName("Energy.Active.Import.Register") EnergyActiveImportRegister,
    @SerialName("Energy.Reactive.Export.Register") EnergyReactiveExportRegister,
    @SerialName("Energy.Reactive.Import.Register") EnergyReactiveImportRegister,
    @SerialName("Energy.Active.Export.Interval") EnergyActiveExportInterval,
    @SerialName("Energy.Active.Import.Interval") EnergyActiveImportInterval,
    @SerialName("Energy.Reactive.Export.Interval") EnergyReactiveExportInterval,
    @SerialName("Energy.Reactive.Import.Interval") EnergyReactiveImportInterval,
    @SerialName("Power.Active.Export") PowerActiveExport,
    @SerialName("Power.Active.Import") PowerActiveImport,
    @SerialName("Power.Offered") PowerOffered,
    @SerialName("Power.Reactive.Export") PowerReactiveExport,
    @SerialName("Power.Reactive.Import") PowerReactiveImport,
    @SerialName("Power.Factor") PowerFactor,
    @SerialName("Current.Import") CurrentImport,
    @SerialName("Current.Export") CurrentExport,
    @SerialName("Current.Offered") CurrentOffered,
    @SerialName("Voltage") Voltage,
    @SerialName("Frequency") Frequency,
    @SerialName("Temperature") Temperature,
    @SerialName("SoC") SoC,
    @SerialName("RPM") RPM
}

/**
 * Reading context enumeration.
 */
@Serializable
enum class ReadingContext {
    @SerialName("Interruption.Begin") InterruptionBegin,
    @SerialName("Interruption.End") InterruptionEnd,
    @SerialName("Sample.Clock") SampleClock,
    @SerialName("Sample.Periodic") SamplePeriodic,
    @SerialName("Transaction.Begin") TransactionBegin,
    @SerialName("Transaction.End") TransactionEnd,
    @SerialName("Trigger") Trigger,
    @SerialName("Other") Other
}

/**
 * Value format enumeration.
 */
@Serializable
enum class ValueFormat {
    @SerialName("Raw") Raw,
    @SerialName("SignedData") SignedData
}

/**
 * Phase enumeration.
 */
@Serializable
enum class Phase {
    @SerialName("L1") L1,
    @SerialName("L2") L2,
    @SerialName("L3") L3,
    @SerialName("N") N,
    @SerialName("L1-N") L1N,
    @SerialName("L2-N") L2N,
    @SerialName("L3-N") L3N,
    @SerialName("L1-L2") L1L2,
    @SerialName("L2-L3") L2L3,
    @SerialName("L3-L1") L3L1
}

/**
 * Location enumeration.
 */
@Serializable
enum class Location {
    @SerialName("Cable") Cable,
    @SerialName("EV") EV,
    @SerialName("Inlet") Inlet,
    @SerialName("Outlet") Outlet,
    @SerialName("Body") Body
}

/**
 * Unit of measure enumeration.
 */
@Serializable
enum class UnitOfMeasure {
    @SerialName("Wh") Wh,
    @SerialName("kWh") kWh,
    @SerialName("varh") varh,
    @SerialName("kvarh") kvarh,
    @SerialName("W") W,
    @SerialName("kW") kW,
    @SerialName("VA") VA,
    @SerialName("kVA") kVA,
    @SerialName("var") VAr,
    @SerialName("kvar") kvar,
    @SerialName("A") A,
    @SerialName("V") V,
    @SerialName("K") K,
    @SerialName("Celcius") Celsius,
    @SerialName("Fahrenheit") Fahrenheit,
    @SerialName("Percent") Percent
}

/**
 * Message trigger enumeration.
 */
@Serializable
enum class MessageTrigger {
    @SerialName("BootNotification") BootNotification,
    @SerialName("DiagnosticsStatusNotification") DiagnosticsStatusNotification,
    @SerialName("FirmwareStatusNotification") FirmwareStatusNotification,
    @SerialName("Heartbeat") Heartbeat,
    @SerialName("MeterValues") MeterValues,
    @SerialName("StatusNotification") StatusNotification
}

/**
 * Trigger message status enumeration.
 */
@Serializable
enum class TriggerMessageStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("NotImplemented") NotImplemented
}

/**
 * Reservation status enumeration.
 */
@Serializable
enum class ReservationStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Faulted") Faulted,
    @SerialName("Occupied") Occupied,
    @SerialName("Rejected") Rejected,
    @SerialName("Unavailable") Unavailable
}

/**
 * Cancel reservation status enumeration.
 */
@Serializable
enum class CancelReservationStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * Firmware status enumeration.
 */
@Serializable
enum class FirmwareStatus {
    @SerialName("Downloaded") Downloaded,
    @SerialName("DownloadFailed") DownloadFailed,
    @SerialName("Downloading") Downloading,
    @SerialName("Idle") Idle,
    @SerialName("InstallationFailed") InstallationFailed,
    @SerialName("Installing") Installing,
    @SerialName("Installed") Installed
}

/**
 * Diagnostics status enumeration.
 */
@Serializable
enum class DiagnosticsStatus {
    @SerialName("Idle") Idle,
    @SerialName("Uploaded") Uploaded,
    @SerialName("UploadFailed") UploadFailed,
    @SerialName("Uploading") Uploading
}

/**
 * Configuration status enumeration.
 */
@Serializable
enum class ConfigurationStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("RebootRequired") RebootRequired,
    @SerialName("NotSupported") NotSupported
}

/**
 * Data transfer status enumeration.
 */
@Serializable
enum class DataTransferStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("UnknownMessageId") UnknownMessageId,
    @SerialName("UnknownVendorId") UnknownVendorId
}

/**
 * Update type enumeration for local list.
 */
@Serializable
enum class UpdateType {
    @SerialName("Differential") Differential,
    @SerialName("Full") Full
}

/**
 * Update status enumeration.
 */
@Serializable
enum class UpdateStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Failed") Failed,
    @SerialName("NotSupported") NotSupported,
    @SerialName("VersionMismatch") VersionMismatch
}

/**
 * Clear cache status enumeration.
 */
@Serializable
enum class ClearCacheStatus {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}
