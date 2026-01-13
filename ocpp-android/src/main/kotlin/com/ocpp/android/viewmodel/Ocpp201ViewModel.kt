package com.ocpp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocpp.core.transport.ConnectionState
import com.ocpp.v201.client.Ocpp201Client
import com.ocpp.v201.messages.*
import com.ocpp.v201.types.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Base ViewModel for OCPP 2.0.1 operations.
 *
 * Provides convenience methods for common OCPP operations with proper
 * ViewModel scope management. Extend this class to create your own
 * charging station ViewModel.
 *
 * Example usage:
 * ```kotlin
 * class ChargingStationViewModel : Ocpp201ViewModel() {
 *     
 *     fun registerStation(model: String, vendor: String) {
 *         viewModelScope.launch {
 *             val result = bootNotification(model, vendor, BootReasonEnumType.PowerUp)
 *             result.onSuccess { response ->
 *                 if (response.status == RegistrationStatusEnumType.Accepted) {
 *                     startHeartbeat(response.interval)
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 */
open class Ocpp201ViewModel(
    private val ocppClient: Ocpp201Client = Ocpp201Client()
) : ViewModel() {
    
    /**
     * Current connection state as StateFlow.
     */
    val connectionState: StateFlow<ConnectionState> = ocppClient.connectionState
        .stateIn(viewModelScope, SharingStarted.Eagerly, ConnectionState.Disconnected)
    
    /**
     * Connect to CSMS.
     */
    fun connect(url: String, chargePointId: String) {
        viewModelScope.launch {
            ocppClient.connect(url, chargePointId)
        }
    }
    
    /**
     * Disconnect from CSMS.
     */
    fun disconnect() {
        viewModelScope.launch {
            ocppClient.disconnect()
        }
    }
    
    /**
     * Send BootNotification.
     */
    suspend fun bootNotification(
        model: String,
        vendorName: String,
        reason: BootReasonEnumType,
        serialNumber: String? = null,
        firmwareVersion: String? = null,
        timeout: Duration = 30.seconds
    ): Result<BootNotificationResponse> {
        val chargingStation = ChargingStationType(
            model = model,
            vendorName = vendorName,
            serialNumber = serialNumber,
            firmwareVersion = firmwareVersion
        )
        return ocppClient.bootNotification(chargingStation, reason, timeout)
    }
    
    /**
     * Send Heartbeat.
     */
    suspend fun heartbeat(timeout: Duration = 30.seconds): Result<HeartbeatResponse> {
        return ocppClient.heartbeat(timeout)
    }
    
    /**
     * Send Authorize.
     */
    suspend fun authorize(
        idToken: String,
        tokenType: IdTokenEnumType = IdTokenEnumType.ISO14443,
        timeout: Duration = 30.seconds
    ): Result<AuthorizeResponse> {
        val token = IdTokenType(idToken = idToken, type = tokenType)
        return ocppClient.authorize(token, timeout = timeout)
    }
    
    /**
     * Send StatusNotification.
     */
    suspend fun statusNotification(
        evseId: Int,
        connectorId: Int,
        status: ConnectorStatusEnumType,
        timestamp: String,
        timeout: Duration = 30.seconds
    ): Result<StatusNotificationResponse> {
        return ocppClient.statusNotification(timestamp, status, evseId, connectorId, timeout)
    }
    
    /**
     * Send TransactionEvent for transaction start.
     */
    suspend fun startTransaction(
        transactionId: String,
        evseId: Int,
        connectorId: Int,
        idToken: String,
        timestamp: String,
        meterValue: Double? = null,
        timeout: Duration = 30.seconds
    ): Result<TransactionEventResponse> {
        return ocppClient.transactionEvent(
            eventType = TransactionEventEnumType.Started,
            timestamp = timestamp,
            triggerReason = TriggerReasonEnumType.Authorized,
            seqNo = 0,
            transactionInfo = TransactionType(
                transactionId = transactionId,
                chargingState = ChargingStateEnumType.Charging
            ),
            evse = EVSEType(id = evseId, connectorId = connectorId),
            idToken = IdTokenType(idToken = idToken, type = IdTokenEnumType.ISO14443),
            meterValue = meterValue?.let {
                listOf(
                    MeterValueType(
                        timestamp = timestamp,
                        sampledValue = listOf(
                            SampledValueType(
                                value = it,
                                measurand = MeasurandEnumType.EnergyActiveImportRegister
                            )
                        )
                    )
                )
            },
            timeout = timeout
        )
    }
    
    /**
     * Send TransactionEvent for transaction end.
     */
    suspend fun stopTransaction(
        transactionId: String,
        timestamp: String,
        stoppedReason: ReasonEnumType,
        meterValue: Double? = null,
        seqNo: Int = 1,
        timeout: Duration = 30.seconds
    ): Result<TransactionEventResponse> {
        return ocppClient.transactionEvent(
            eventType = TransactionEventEnumType.Ended,
            timestamp = timestamp,
            triggerReason = TriggerReasonEnumType.StopAuthorized,
            seqNo = seqNo,
            transactionInfo = TransactionType(
                transactionId = transactionId,
                stoppedReason = stoppedReason
            ),
            meterValue = meterValue?.let {
                listOf(
                    MeterValueType(
                        timestamp = timestamp,
                        sampledValue = listOf(
                            SampledValueType(
                                value = it,
                                measurand = MeasurandEnumType.EnergyActiveImportRegister
                            )
                        )
                    )
                )
            },
            timeout = timeout
        )
    }
    
    /**
     * Send MeterValues.
     */
    suspend fun sendMeterValues(
        evseId: Int,
        timestamp: String,
        energyValue: Double,
        powerValue: Double? = null,
        timeout: Duration = 30.seconds
    ): Result<MeterValuesResponse> {
        val sampledValues = mutableListOf(
            SampledValueType(
                value = energyValue,
                measurand = MeasurandEnumType.EnergyActiveImportRegister
            )
        )
        
        powerValue?.let {
            sampledValues.add(
                SampledValueType(
                    value = it,
                    measurand = MeasurandEnumType.PowerActiveImport
                )
            )
        }
        
        return ocppClient.meterValues(
            evseId = evseId,
            meterValue = listOf(
                MeterValueType(
                    timestamp = timestamp,
                    sampledValue = sampledValues
                )
            ),
            timeout = timeout
        )
    }
    
    /**
     * Send DataTransfer for custom vendor data.
     */
    suspend fun sendDataTransfer(
        vendorId: String,
        messageId: String? = null,
        data: String? = null,
        timeout: Duration = 30.seconds
    ): Result<DataTransferResponse> {
        return ocppClient.dataTransfer(vendorId, messageId, data, timeout)
    }
    
    /**
     * Get the underlying client for advanced operations.
     */
    fun getClient(): Ocpp201Client = ocppClient
    
    override fun onCleared() {
        super.onCleared()
        // Disconnect when ViewModel is cleared
        viewModelScope.launch {
            ocppClient.disconnect()
        }
    }
}
