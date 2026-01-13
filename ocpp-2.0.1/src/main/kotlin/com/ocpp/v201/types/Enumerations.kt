package com.ocpp.v201.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================================
// OCPP 2.0.1 Enumerations
// ============================================================================

/**
 * Boot reason enumeration - Reason for sending a BootNotification.
 */
@Serializable
enum class BootReasonEnumType {
    @SerialName("ApplicationReset") ApplicationReset,
    @SerialName("FirmwareUpdate") FirmwareUpdate,
    @SerialName("LocalReset") LocalReset,
    @SerialName("PowerUp") PowerUp,
    @SerialName("RemoteReset") RemoteReset,
    @SerialName("ScheduledReset") ScheduledReset,
    @SerialName("Triggered") Triggered,
    @SerialName("Unknown") Unknown,
    @SerialName("Watchdog") Watchdog
}

/**
 * Registration status - Status returned in response to BootNotification.
 */
@Serializable
enum class RegistrationStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Pending") Pending,
    @SerialName("Rejected") Rejected
}

/**
 * Authorization status enumeration.
 */
@Serializable
enum class AuthorizationStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Blocked") Blocked,
    @SerialName("ConcurrentTx") ConcurrentTx,
    @SerialName("Expired") Expired,
    @SerialName("Invalid") Invalid,
    @SerialName("NoCredit") NoCredit,
    @SerialName("NotAllowedTypeEVSE") NotAllowedTypeEVSE,
    @SerialName("NotAtThisLocation") NotAtThisLocation,
    @SerialName("NotAtThisTime") NotAtThisTime,
    @SerialName("Unknown") Unknown
}

/**
 * IdToken type enumeration.
 */
@Serializable
enum class IdTokenEnumType {
    @SerialName("Central") Central,
    @SerialName("eMAID") eMAID,
    @SerialName("ISO14443") ISO14443,
    @SerialName("ISO15693") ISO15693,
    @SerialName("KeyCode") KeyCode,
    @SerialName("Local") Local,
    @SerialName("MacAddress") MacAddress,
    @SerialName("NoAuthorization") NoAuthorization
}

/**
 * Transaction event enumeration.
 */
@Serializable
enum class TransactionEventEnumType {
    @SerialName("Started") Started,
    @SerialName("Updated") Updated,
    @SerialName("Ended") Ended
}

/**
 * Trigger reason for transaction events.
 */
@Serializable
enum class TriggerReasonEnumType {
    @SerialName("Authorized") Authorized,
    @SerialName("CablePluggedIn") CablePluggedIn,
    @SerialName("ChargingRateChanged") ChargingRateChanged,
    @SerialName("ChargingStateChanged") ChargingStateChanged,
    @SerialName("Deauthorized") Deauthorized,
    @SerialName("EnergyLimitReached") EnergyLimitReached,
    @SerialName("EVCommunicationLost") EVCommunicationLost,
    @SerialName("EVConnectTimeout") EVConnectTimeout,
    @SerialName("EVDeparted") EVDeparted,
    @SerialName("EVDetected") EVDetected,
    @SerialName("MeterValueClock") MeterValueClock,
    @SerialName("MeterValuePeriodic") MeterValuePeriodic,
    @SerialName("RemoteStart") RemoteStart,
    @SerialName("RemoteStop") RemoteStop,
    @SerialName("ResetCommand") ResetCommand,
    @SerialName("SignedDataReceived") SignedDataReceived,
    @SerialName("StopAuthorized") StopAuthorized,
    @SerialName("TimeLimitReached") TimeLimitReached,
    @SerialName("Trigger") Trigger,
    @SerialName("UnlockCommand") UnlockCommand
}

/**
 * Charging state enumeration.
 */
@Serializable
enum class ChargingStateEnumType {
    @SerialName("Charging") Charging,
    @SerialName("EVConnected") EVConnected,
    @SerialName("SuspendedEV") SuspendedEV,
    @SerialName("SuspendedEVSE") SuspendedEVSE,
    @SerialName("Idle") Idle
}

/**
 * Reason for stopping a transaction.
 */
@Serializable
enum class ReasonEnumType {
    @SerialName("DeAuthorized") DeAuthorized,
    @SerialName("EmergencyStop") EmergencyStop,
    @SerialName("EnergyLimitReached") EnergyLimitReached,
    @SerialName("EVDisconnected") EVDisconnected,
    @SerialName("GroundFault") GroundFault,
    @SerialName("ImmediateReset") ImmediateReset,
    @SerialName("Local") Local,
    @SerialName("LocalOutOfCredit") LocalOutOfCredit,
    @SerialName("MasterPass") MasterPass,
    @SerialName("Other") Other,
    @SerialName("OvercurrentFault") OvercurrentFault,
    @SerialName("PowerLoss") PowerLoss,
    @SerialName("PowerQuality") PowerQuality,
    @SerialName("Reboot") Reboot,
    @SerialName("Remote") Remote,
    @SerialName("SOCLimitReached") SOCLimitReached,
    @SerialName("StoppedByEV") StoppedByEV,
    @SerialName("TimeLimitReached") TimeLimitReached,
    @SerialName("Timeout") Timeout
}

/**
 * Connector status enumeration.
 */
@Serializable
enum class ConnectorStatusEnumType {
    @SerialName("Available") Available,
    @SerialName("Occupied") Occupied,
    @SerialName("Reserved") Reserved,
    @SerialName("Unavailable") Unavailable,
    @SerialName("Faulted") Faulted
}

/**
 * Operational status enumeration.
 */
@Serializable
enum class OperationalStatusEnumType {
    @SerialName("Inoperative") Inoperative,
    @SerialName("Operative") Operative
}

/**
 * Change availability status enumeration.
 */
@Serializable
enum class ChangeAvailabilityStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("Scheduled") Scheduled
}

/**
 * Reset type enumeration.
 */
@Serializable
enum class ResetEnumType {
    @SerialName("Immediate") Immediate,
    @SerialName("OnIdle") OnIdle
}

/**
 * Reset status enumeration.
 */
@Serializable
enum class ResetStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("Scheduled") Scheduled
}

/**
 * Unlock status enumeration.
 */
@Serializable
enum class UnlockStatusEnumType {
    @SerialName("Unlocked") Unlocked,
    @SerialName("UnlockFailed") UnlockFailed,
    @SerialName("OngoingAuthorizedTransaction") OngoingAuthorizedTransaction,
    @SerialName("UnknownConnector") UnknownConnector
}

/**
 * Request start stop status enumeration.
 */
@Serializable
enum class RequestStartStopStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * Charging profile purpose enumeration.
 */
@Serializable
enum class ChargingProfilePurposeEnumType {
    @SerialName("ChargingStationExternalConstraints") ChargingStationExternalConstraints,
    @SerialName("ChargingStationMaxProfile") ChargingStationMaxProfile,
    @SerialName("TxDefaultProfile") TxDefaultProfile,
    @SerialName("TxProfile") TxProfile
}

/**
 * Charging profile kind enumeration.
 */
@Serializable
enum class ChargingProfileKindEnumType {
    @SerialName("Absolute") Absolute,
    @SerialName("Recurring") Recurring,
    @SerialName("Relative") Relative
}

/**
 * Recurrency kind enumeration.
 */
@Serializable
enum class RecurrencyKindEnumType {
    @SerialName("Daily") Daily,
    @SerialName("Weekly") Weekly
}

/**
 * Charging profile status enumeration.
 */
@Serializable
enum class ChargingProfileStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * Clear charging profile status enumeration.
 */
@Serializable
enum class ClearChargingProfileStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Unknown") Unknown
}

/**
 * Charging rate unit enumeration.
 */
@Serializable
enum class ChargingRateUnitEnumType {
    @SerialName("W") W,
    @SerialName("A") A
}

/**
 * Measurand enumeration.
 */
@Serializable
enum class MeasurandEnumType {
    @SerialName("Current.Export") CurrentExport,
    @SerialName("Current.Import") CurrentImport,
    @SerialName("Current.Offered") CurrentOffered,
    @SerialName("Energy.Active.Export.Register") EnergyActiveExportRegister,
    @SerialName("Energy.Active.Import.Register") EnergyActiveImportRegister,
    @SerialName("Energy.Reactive.Export.Register") EnergyReactiveExportRegister,
    @SerialName("Energy.Reactive.Import.Register") EnergyReactiveImportRegister,
    @SerialName("Energy.Active.Export.Interval") EnergyActiveExportInterval,
    @SerialName("Energy.Active.Import.Interval") EnergyActiveImportInterval,
    @SerialName("Energy.Active.Net") EnergyActiveNet,
    @SerialName("Energy.Reactive.Export.Interval") EnergyReactiveExportInterval,
    @SerialName("Energy.Reactive.Import.Interval") EnergyReactiveImportInterval,
    @SerialName("Energy.Reactive.Net") EnergyReactiveNet,
    @SerialName("Energy.Apparent.Net") EnergyApparentNet,
    @SerialName("Energy.Apparent.Import") EnergyApparentImport,
    @SerialName("Energy.Apparent.Export") EnergyApparentExport,
    @SerialName("Frequency") Frequency,
    @SerialName("Power.Active.Export") PowerActiveExport,
    @SerialName("Power.Active.Import") PowerActiveImport,
    @SerialName("Power.Factor") PowerFactor,
    @SerialName("Power.Offered") PowerOffered,
    @SerialName("Power.Reactive.Export") PowerReactiveExport,
    @SerialName("Power.Reactive.Import") PowerReactiveImport,
    @SerialName("SoC") SoC,
    @SerialName("Voltage") Voltage
}

/**
 * Reading context enumeration.
 */
@Serializable
enum class ReadingContextEnumType {
    @SerialName("Interruption.Begin") InterruptionBegin,
    @SerialName("Interruption.End") InterruptionEnd,
    @SerialName("Other") Other,
    @SerialName("Sample.Clock") SampleClock,
    @SerialName("Sample.Periodic") SamplePeriodic,
    @SerialName("Transaction.Begin") TransactionBegin,
    @SerialName("Transaction.End") TransactionEnd,
    @SerialName("Trigger") Trigger
}

/**
 * Phase enumeration.
 */
@Serializable
enum class PhaseEnumType {
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
enum class LocationEnumType {
    @SerialName("Body") Body,
    @SerialName("Cable") Cable,
    @SerialName("EV") EV,
    @SerialName("Inlet") Inlet,
    @SerialName("Outlet") Outlet
}

/**
 * Message trigger enumeration.
 */
@Serializable
enum class MessageTriggerEnumType {
    @SerialName("BootNotification") BootNotification,
    @SerialName("LogStatusNotification") LogStatusNotification,
    @SerialName("FirmwareStatusNotification") FirmwareStatusNotification,
    @SerialName("Heartbeat") Heartbeat,
    @SerialName("MeterValues") MeterValues,
    @SerialName("SignChargingStationCertificate") SignChargingStationCertificate,
    @SerialName("SignV2GCertificate") SignV2GCertificate,
    @SerialName("StatusNotification") StatusNotification,
    @SerialName("TransactionEvent") TransactionEvent,
    @SerialName("SignCombinedCertificate") SignCombinedCertificate,
    @SerialName("PublishFirmwareStatusNotification") PublishFirmwareStatusNotification
}

/**
 * Trigger message status enumeration.
 */
@Serializable
enum class TriggerMessageStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("NotImplemented") NotImplemented
}

/**
 * Reservation status enumeration.
 */
@Serializable
enum class ReserveNowStatusEnumType {
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
enum class CancelReservationStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected
}

/**
 * Firmware status enumeration.
 */
@Serializable
enum class FirmwareStatusEnumType {
    @SerialName("Downloaded") Downloaded,
    @SerialName("DownloadFailed") DownloadFailed,
    @SerialName("Downloading") Downloading,
    @SerialName("DownloadScheduled") DownloadScheduled,
    @SerialName("DownloadPaused") DownloadPaused,
    @SerialName("Idle") Idle,
    @SerialName("InstallationFailed") InstallationFailed,
    @SerialName("Installing") Installing,
    @SerialName("Installed") Installed,
    @SerialName("InstallRebooting") InstallRebooting,
    @SerialName("InstallScheduled") InstallScheduled,
    @SerialName("InstallVerificationFailed") InstallVerificationFailed,
    @SerialName("InvalidSignature") InvalidSignature,
    @SerialName("SignatureVerified") SignatureVerified
}

/**
 * Update firmware status enumeration.
 */
@Serializable
enum class UpdateFirmwareStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("AcceptedCanceled") AcceptedCanceled,
    @SerialName("InvalidCertificate") InvalidCertificate,
    @SerialName("RevokedCertificate") RevokedCertificate
}

/**
 * Get variable status enumeration.
 */
@Serializable
enum class GetVariableStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("UnknownComponent") UnknownComponent,
    @SerialName("UnknownVariable") UnknownVariable,
    @SerialName("NotSupportedAttributeType") NotSupportedAttributeType
}

/**
 * Set variable status enumeration.
 */
@Serializable
enum class SetVariableStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("UnknownComponent") UnknownComponent,
    @SerialName("UnknownVariable") UnknownVariable,
    @SerialName("NotSupportedAttributeType") NotSupportedAttributeType,
    @SerialName("RebootRequired") RebootRequired
}

/**
 * Attribute enumeration.
 */
@Serializable
enum class AttributeEnumType {
    @SerialName("Actual") Actual,
    @SerialName("Target") Target,
    @SerialName("MinSet") MinSet,
    @SerialName("MaxSet") MaxSet
}

/**
 * Data transfer status enumeration.
 */
@Serializable
enum class DataTransferStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("UnknownMessageId") UnknownMessageId,
    @SerialName("UnknownVendorId") UnknownVendorId
}

/**
 * Certificate status enumeration.
 */
@Serializable
enum class AuthorizeCertificateStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("SignatureError") SignatureError,
    @SerialName("CertificateExpired") CertificateExpired,
    @SerialName("CertificateRevoked") CertificateRevoked,
    @SerialName("NoCertificateAvailable") NoCertificateAvailable,
    @SerialName("CertChainError") CertChainError,
    @SerialName("ContractCancelled") ContractCancelled
}

/**
 * Hash algorithm enumeration.
 */
@Serializable
enum class HashAlgorithmEnumType {
    @SerialName("SHA256") SHA256,
    @SerialName("SHA384") SHA384,
    @SerialName("SHA512") SHA512
}

/**
 * Certificate use enumeration.
 */
@Serializable
enum class CertificateSigningUseEnumType {
    @SerialName("ChargingStationCertificate") ChargingStationCertificate,
    @SerialName("V2GCertificate") V2GCertificate
}

/**
 * Install certificate status enumeration.
 */
@Serializable
enum class InstallCertificateStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("Failed") Failed
}

/**
 * Install certificate use enumeration.
 */
@Serializable
enum class InstallCertificateUseEnumType {
    @SerialName("V2GRootCertificate") V2GRootCertificate,
    @SerialName("MORootCertificate") MORootCertificate,
    @SerialName("CSMSRootCertificate") CSMSRootCertificate,
    @SerialName("ManufacturerRootCertificate") ManufacturerRootCertificate
}

/**
 * Delete certificate status enumeration.
 */
@Serializable
enum class DeleteCertificateStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Failed") Failed,
    @SerialName("NotFound") NotFound
}

/**
 * Message format enumeration.
 */
@Serializable
enum class MessageFormatEnumType {
    @SerialName("ASCII") ASCII,
    @SerialName("HTML") HTML,
    @SerialName("URI") URI,
    @SerialName("UTF8") UTF8
}

/**
 * Message priority enumeration.
 */
@Serializable
enum class MessagePriorityEnumType {
    @SerialName("AlwaysFront") AlwaysFront,
    @SerialName("InFront") InFront,
    @SerialName("NormalCycle") NormalCycle
}

/**
 * Message state enumeration.
 */
@Serializable
enum class MessageStateEnumType {
    @SerialName("Charging") Charging,
    @SerialName("Faulted") Faulted,
    @SerialName("Idle") Idle,
    @SerialName("Unavailable") Unavailable
}

/**
 * Display message status enumeration.
 */
@Serializable
enum class DisplayMessageStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("NotSupportedMessageFormat") NotSupportedMessageFormat,
    @SerialName("Rejected") Rejected,
    @SerialName("NotSupportedPriority") NotSupportedPriority,
    @SerialName("NotSupportedState") NotSupportedState,
    @SerialName("UnknownTransaction") UnknownTransaction
}

/**
 * Clear message status enumeration.
 */
@Serializable
enum class ClearMessageStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Unknown") Unknown
}

/**
 * Report base enumeration.
 */
@Serializable
enum class ReportBaseEnumType {
    @SerialName("ConfigurationInventory") ConfigurationInventory,
    @SerialName("FullInventory") FullInventory,
    @SerialName("SummaryInventory") SummaryInventory
}

/**
 * Generic device model status enumeration.
 */
@Serializable
enum class GenericDeviceModelStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("NotSupported") NotSupported,
    @SerialName("EmptyResultSet") EmptyResultSet
}

/**
 * Log enumeration.
 */
@Serializable
enum class LogEnumType {
    @SerialName("DiagnosticsLog") DiagnosticsLog,
    @SerialName("SecurityLog") SecurityLog
}

/**
 * Log status enumeration.
 */
@Serializable
enum class LogStatusEnumType {
    @SerialName("Accepted") Accepted,
    @SerialName("Rejected") Rejected,
    @SerialName("AcceptedCanceled") AcceptedCanceled
}

/**
 * Upload log status enumeration.
 */
@Serializable
enum class UploadLogStatusEnumType {
    @SerialName("BadMessage") BadMessage,
    @SerialName("Idle") Idle,
    @SerialName("NotSupportedOperation") NotSupportedOperation,
    @SerialName("PermissionDenied") PermissionDenied,
    @SerialName("Uploaded") Uploaded,
    @SerialName("UploadFailure") UploadFailure,
    @SerialName("Uploading") Uploading,
    @SerialName("AcceptedCanceled") AcceptedCanceled
}
