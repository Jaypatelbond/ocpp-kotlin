package com.ocpp.v16.adapter

import com.ocpp.core.api.*
import com.ocpp.core.transport.ConnectionState
import com.ocpp.core.transport.OcppTransport
import com.ocpp.core.transport.OkHttpOcppTransport
import com.ocpp.core.transport.OcppTransportConfig
import com.ocpp.v16.client.Ocpp16Client
import com.ocpp.v16.types.*
import com.ocpp.v16.messages.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant

/**
 * Generic API Adapter for OCPP 1.6.
 *
 * This adapter implements the [GenericOcppClient] interface using the underlying
 * [Ocpp16Client], allowing version-agnostic code to work with OCPP 1.6.
 *
 * Example:
 * ```kotlin
 * val client: GenericOcppClient = GenericOcpp16Adapter()
 * client.connect("ws://csms.example.com/ocpp/1.6", "CP001")
 *
 * val result = client.bootNotification("Model X", "Vendor Y")
 * if (result.getOrNull()?.accepted == true) {
 *     println("Registered!")
 * }
 * ```
 */
class GenericOcpp16Adapter(
    transport: OcppTransport = OkHttpOcppTransport(
        OcppTransportConfig(subProtocols = listOf("ocpp1.6"))
    ),
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : GenericOcppClient {

    private val client = Ocpp16Client(transport, scope)
    private var currentTransactionId: Int? = null

    override val connectionState: StateFlow<ConnectionState> = client.connectionState

    override val version: OcppVersion = OcppVersion.V16

    override suspend fun connect(url: String, chargePointId: String) {
        client.connect(url, chargePointId)
    }

    override suspend fun disconnect() {
        client.disconnect()
    }

    override suspend fun bootNotification(
        model: String,
        vendor: String,
        serialNumber: String?,
        firmwareVersion: String?
    ): Result<BootNotificationResult> {
        return client.bootNotification(
            chargePointModel = model,
            chargePointVendor = vendor,
            chargePointSerialNumber = serialNumber,
            firmwareVersion = firmwareVersion
        ).map { response ->
            BootNotificationResult(
                accepted = response.status == RegistrationStatus.Accepted,
                heartbeatIntervalSeconds = response.interval,
                currentTime = response.currentTime
            )
        }
    }

    override suspend fun heartbeat(): Result<HeartbeatResult> {
        return client.heartbeat().map { response ->
            HeartbeatResult(currentTime = response.currentTime)
        }
    }

    override suspend fun authorize(idToken: String): Result<AuthorizationResult> {
        return client.authorize(idTag = idToken).map { response ->
            AuthorizationResult(
                accepted = response.idTagInfo.status == AuthorizationStatus.Accepted,
                expiryDate = response.idTagInfo.expiryDate
            )
        }
    }

    override suspend fun statusNotification(
        connectorId: Int,
        status: GenericConnectorStatus,
        errorCode: String?
    ): Result<Unit> {
        return client.statusNotification(
            connectorId = connectorId,
            status = status.toOcpp16(),
            errorCode = ChargePointErrorCode.NoError,
            timestamp = Instant.now().toString()
        ).map { }
    }

    override suspend fun startTransaction(
        connectorId: Int,
        idToken: String,
        meterStart: Int
    ): Result<TransactionResult> {
        return client.startTransaction(
            connectorId = connectorId,
            idTag = idToken,
            meterStart = meterStart,
            timestamp = Instant.now().toString()
        ).map { response ->
            currentTransactionId = response.transactionId
            TransactionResult(
                transactionId = response.transactionId.toString(),
                authorized = response.idTagInfo.status == AuthorizationStatus.Accepted
            )
        }
    }

    override suspend fun stopTransaction(
        transactionId: String,
        meterStop: Int,
        reason: StopReason?
    ): Result<Unit> {
        return client.stopTransaction(
            meterStop = meterStop,
            timestamp = Instant.now().toString(),
            transactionId = transactionId.toIntOrNull() ?: currentTransactionId ?: 0,
            reason = reason?.toOcpp16()
        ).map { 
            currentTransactionId = null
        }
    }

    override suspend fun meterValues(
        connectorId: Int,
        transactionId: String?,
        energyWh: Int,
        powerW: Int?
    ): Result<Unit> {
        val sampledValues = mutableListOf(
            SampledValue(
                value = energyWh.toString(),
                unit = UnitOfMeasure.Wh,
                measurand = Measurand.EnergyActiveImportRegister
            )
        )

        powerW?.let {
            sampledValues.add(
                SampledValue(
                    value = it.toString(),
                    unit = UnitOfMeasure.W,
                    measurand = Measurand.PowerActiveImport
                )
            )
        }

        return client.meterValues(
            connectorId = connectorId,
            meterValue = listOf(
                MeterValue(
                    timestamp = Instant.now().toString(),
                    sampledValue = sampledValues
                )
            ),
            transactionId = transactionId?.toIntOrNull() ?: currentTransactionId
        ).map { }
    }

    override fun onRemoteStart(handler: suspend (RemoteStartRequest) -> RemoteStartResponse) {
        client.onRemoteStartTransaction { request ->
            val genericRequest = RemoteStartRequest(
                idToken = request.idTag,
                connectorId = request.connectorId
            )
            val response = handler(genericRequest)
            RemoteStartTransactionResponse(
                status = if (response.accepted) {
                    RemoteStartStopStatus.Accepted
                } else {
                    RemoteStartStopStatus.Rejected
                }
            )
        }
    }

    override fun onRemoteStop(handler: suspend (RemoteStopRequest) -> RemoteStopResponse) {
        client.onRemoteStopTransaction { request ->
            val genericRequest = RemoteStopRequest(transactionId = request.transactionId.toString())
            val response = handler(genericRequest)
            RemoteStopTransactionResponse(
                status = if (response.accepted) {
                    RemoteStartStopStatus.Accepted
                } else {
                    RemoteStartStopStatus.Rejected
                }
            )
        }
    }

    override fun onReset(handler: suspend (com.ocpp.core.api.ResetRequest) -> com.ocpp.core.api.ResetResponse) {
        client.onReset { request ->
            val genericRequest = com.ocpp.core.api.ResetRequest(hard = request.type == ResetType.Hard)
            val response = handler(genericRequest)
            com.ocpp.v16.messages.ResetResponse(
                status = if (response.accepted) {
                    ResetStatus.Accepted
                } else {
                    ResetStatus.Rejected
                }
            )
        }
    }

    // ==================== Extension Functions ====================

    private fun GenericConnectorStatus.toOcpp16(): ChargePointStatus = when (this) {
        GenericConnectorStatus.Available -> ChargePointStatus.Available
        GenericConnectorStatus.Preparing -> ChargePointStatus.Preparing
        GenericConnectorStatus.Charging -> ChargePointStatus.Charging
        GenericConnectorStatus.SuspendedEVSE -> ChargePointStatus.SuspendedEVSE
        GenericConnectorStatus.SuspendedEV -> ChargePointStatus.SuspendedEV
        GenericConnectorStatus.Finishing -> ChargePointStatus.Finishing
        GenericConnectorStatus.Reserved -> ChargePointStatus.Reserved
        GenericConnectorStatus.Unavailable -> ChargePointStatus.Unavailable
        GenericConnectorStatus.Faulted -> ChargePointStatus.Faulted
    }

    private fun StopReason.toOcpp16(): Reason = when (this) {
        StopReason.DeAuthorized -> Reason.DeAuthorized
        StopReason.EmergencyStop -> Reason.EmergencyStop
        StopReason.EVDisconnected -> Reason.EVDisconnected
        StopReason.HardReset -> Reason.HardReset
        StopReason.Local -> Reason.Local
        StopReason.Other -> Reason.Other
        StopReason.PowerLoss -> Reason.PowerLoss
        StopReason.Reboot -> Reason.Reboot
        StopReason.Remote -> Reason.Remote
        StopReason.SoftReset -> Reason.SoftReset
        StopReason.UnlockCommand -> Reason.UnlockCommand
    }
}
