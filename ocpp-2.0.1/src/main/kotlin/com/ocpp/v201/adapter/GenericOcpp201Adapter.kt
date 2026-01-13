package com.ocpp.v201.adapter

import com.ocpp.core.api.*
import com.ocpp.core.transport.ConnectionState
import com.ocpp.core.transport.OcppTransport
import com.ocpp.core.transport.OkHttpOcppTransport
import com.ocpp.v201.client.Ocpp201Client
import com.ocpp.v201.messages.*
import com.ocpp.v201.types.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.util.UUID

/**
 * Generic API Adapter for OCPP 2.0.1.
 *
 * This adapter implements the [GenericOcppClient] interface using the underlying
 * [Ocpp201Client], allowing version-agnostic code to work with OCPP 2.0.1.
 *
 * Example:
 * ```kotlin
 * val client: GenericOcppClient = GenericOcpp201Adapter()
 * client.connect("ws://csms.example.com/ocpp", "CP001")
 *
 * val result = client.bootNotification("Model X", "Vendor Y")
 * if (result.getOrNull()?.accepted == true) {
 *     println("Registered!")
 * }
 * ```
 */
class GenericOcpp201Adapter(
    transport: OcppTransport = OkHttpOcppTransport(),
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : GenericOcppClient {

    private val client = Ocpp201Client(transport, scope)
    private var currentEvseId = 1
    private var currentSeqNo = 0

    override val connectionState: StateFlow<ConnectionState> = client.connectionState

    override val version: OcppVersion = OcppVersion.V201

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
            chargingStation = ChargingStationType(
                model = model,
                vendorName = vendor,
                serialNumber = serialNumber,
                firmwareVersion = firmwareVersion
            ),
            reason = BootReasonEnumType.PowerUp
        ).map { response ->
            BootNotificationResult(
                accepted = response.status == RegistrationStatusEnumType.Accepted,
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
        return client.authorize(
            idToken = IdTokenType(
                idToken = idToken,
                type = IdTokenEnumType.ISO14443
            )
        ).map { response ->
            AuthorizationResult(
                accepted = response.idTokenInfo.status == AuthorizationStatusEnumType.Accepted,
                expiryDate = response.idTokenInfo.cacheExpiryDateTime
            )
        }
    }

    override suspend fun statusNotification(
        connectorId: Int,
        status: GenericConnectorStatus,
        errorCode: String?
    ): Result<Unit> {
        return client.statusNotification(
            timestamp = Instant.now().toString(),
            connectorStatus = status.toOcpp201(),
            evseId = currentEvseId,
            connectorId = connectorId
        ).map { }
    }

    override suspend fun startTransaction(
        connectorId: Int,
        idToken: String,
        meterStart: Int
    ): Result<TransactionResult> {
        val transactionId = UUID.randomUUID().toString()
        currentSeqNo = 0

        return client.transactionEvent(
            eventType = TransactionEventEnumType.Started,
            timestamp = Instant.now().toString(),
            triggerReason = TriggerReasonEnumType.Authorized,
            seqNo = currentSeqNo++,
            transactionInfo = TransactionType(transactionId = transactionId),
            evse = EVSEType(id = currentEvseId, connectorId = connectorId),
            idToken = IdTokenType(idToken = idToken, type = IdTokenEnumType.ISO14443),
            meterValue = listOf(
                MeterValueType(
                    timestamp = Instant.now().toString(),
                    sampledValue = listOf(
                        SampledValueType(
                            value = meterStart.toDouble(),
                            measurand = MeasurandEnumType.EnergyActiveImportRegister
                        )
                    )
                )
            )
        ).map {
            TransactionResult(
                transactionId = transactionId,
                authorized = true
            )
        }
    }

    override suspend fun stopTransaction(
        transactionId: String,
        meterStop: Int,
        reason: StopReason?
    ): Result<Unit> {
        return client.transactionEvent(
            eventType = TransactionEventEnumType.Ended,
            timestamp = Instant.now().toString(),
            triggerReason = TriggerReasonEnumType.StopAuthorized,
            seqNo = currentSeqNo++,
            transactionInfo = TransactionType(
                transactionId = transactionId,
                stoppedReason = reason?.toOcpp201()
            ),
            meterValue = listOf(
                MeterValueType(
                    timestamp = Instant.now().toString(),
                    sampledValue = listOf(
                        SampledValueType(
                            value = meterStop.toDouble(),
                            measurand = MeasurandEnumType.EnergyActiveImportRegister
                        )
                    )
                )
            )
        ).map { }
    }

    override suspend fun meterValues(
        connectorId: Int,
        transactionId: String?,
        energyWh: Int,
        powerW: Int?
    ): Result<Unit> {
        val sampledValues = mutableListOf(
            SampledValueType(
                value = energyWh.toDouble(),
                measurand = MeasurandEnumType.EnergyActiveImportRegister
            )
        )

        powerW?.let {
            sampledValues.add(
                SampledValueType(
                    value = it.toDouble(),
                    measurand = MeasurandEnumType.PowerActiveImport
                )
            )
        }

        return if (transactionId != null) {
            client.transactionEvent(
                eventType = TransactionEventEnumType.Updated,
                timestamp = Instant.now().toString(),
                triggerReason = TriggerReasonEnumType.MeterValuePeriodic,
                seqNo = currentSeqNo++,
                transactionInfo = TransactionType(transactionId = transactionId),
                meterValue = listOf(
                    MeterValueType(
                        timestamp = Instant.now().toString(),
                        sampledValue = sampledValues
                    )
                )
            ).map { }
        } else {
            client.meterValues(
                evseId = currentEvseId,
                meterValue = listOf(
                    MeterValueType(
                        timestamp = Instant.now().toString(),
                        sampledValue = sampledValues
                    )
                )
            ).map { }
        }
    }

    override fun onRemoteStart(handler: suspend (RemoteStartRequest) -> RemoteStartResponse) {
        client.onRequestStartTransaction { request ->
            val genericRequest = RemoteStartRequest(
                idToken = request.idToken.idToken,
                connectorId = request.evseId
            )
            val response = handler(genericRequest)
            RequestStartTransactionResponse(
                status = if (response.accepted) {
                    RequestStartStopStatusEnumType.Accepted
                } else {
                    RequestStartStopStatusEnumType.Rejected
                }
            )
        }
    }

    override fun onRemoteStop(handler: suspend (RemoteStopRequest) -> RemoteStopResponse) {
        client.onRequestStopTransaction { request ->
            val genericRequest = RemoteStopRequest(transactionId = request.transactionId)
            val response = handler(genericRequest)
            RequestStopTransactionResponse(
                status = if (response.accepted) {
                    RequestStartStopStatusEnumType.Accepted
                } else {
                    RequestStartStopStatusEnumType.Rejected
                }
            )
        }
    }

    override fun onReset(handler: suspend (com.ocpp.core.api.ResetRequest) -> com.ocpp.core.api.ResetResponse) {
        client.onReset { request ->
            val genericRequest = com.ocpp.core.api.ResetRequest(hard = request.type == ResetEnumType.Immediate)
            val response = handler(genericRequest)
            com.ocpp.v201.messages.ResetResponse(
                status = if (response.accepted) {
                    ResetStatusEnumType.Accepted
                } else {
                    ResetStatusEnumType.Rejected
                }
            )
        }
    }

    // ==================== Extension Functions ====================

    private fun GenericConnectorStatus.toOcpp201(): ConnectorStatusEnumType = when (this) {
        GenericConnectorStatus.Available -> ConnectorStatusEnumType.Available
        GenericConnectorStatus.Preparing -> ConnectorStatusEnumType.Available
        GenericConnectorStatus.Charging -> ConnectorStatusEnumType.Occupied
        GenericConnectorStatus.SuspendedEVSE -> ConnectorStatusEnumType.Occupied
        GenericConnectorStatus.SuspendedEV -> ConnectorStatusEnumType.Occupied
        GenericConnectorStatus.Finishing -> ConnectorStatusEnumType.Occupied
        GenericConnectorStatus.Reserved -> ConnectorStatusEnumType.Reserved
        GenericConnectorStatus.Unavailable -> ConnectorStatusEnumType.Unavailable
        GenericConnectorStatus.Faulted -> ConnectorStatusEnumType.Faulted
    }

    private fun StopReason.toOcpp201(): ReasonEnumType = when (this) {
        StopReason.DeAuthorized -> ReasonEnumType.DeAuthorized
        StopReason.EmergencyStop -> ReasonEnumType.EmergencyStop
        StopReason.EVDisconnected -> ReasonEnumType.EVDisconnected
        StopReason.HardReset -> ReasonEnumType.ImmediateReset
        StopReason.Local -> ReasonEnumType.Local
        StopReason.Other -> ReasonEnumType.Other
        StopReason.PowerLoss -> ReasonEnumType.PowerLoss
        StopReason.Reboot -> ReasonEnumType.Reboot
        StopReason.Remote -> ReasonEnumType.Remote
        StopReason.SoftReset -> ReasonEnumType.Reboot
        StopReason.UnlockCommand -> ReasonEnumType.Other
    }
}
