package com.ocpp.sample

import androidx.lifecycle.viewModelScope
import com.ocpp.android.viewmodel.Ocpp201ViewModel
import com.ocpp.sample.ui.screens.ChargingSession
import com.ocpp.v201.types.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

/**
 * Charging state for UI updates
 */
data class ChargingState(
    val isCharging: Boolean = false,
    val transactionId: String? = null,
    val energyKwh: Double = 0.0,
    val durationMinutes: Int = 0
)

/**
 * ViewModel for the enhanced sample app with session history and Indian currency support.
 */
class MainViewModel : Ocpp201ViewModel() {
    
    private val _logs = MutableStateFlow<List<String>>(listOf("👋 Welcome! Connect to start."))
    val logs: StateFlow<List<String>> = _logs.asStateFlow()
    
    private val _chargingState = MutableStateFlow(ChargingState())
    val chargingState: StateFlow<ChargingState> = _chargingState.asStateFlow()
    
    private val _sessions = MutableStateFlow<List<ChargingSession>>(emptyList())
    val sessions: StateFlow<List<ChargingSession>> = _sessions.asStateFlow()
    
    private var currentEvseId = 1
    private var currentConnectorId = 1
    private var sessionEnergyKwh = 0.0
    private var sessionStartTime: Long = 0
    
    private fun formatCurrency(amount: Double): String = "₹${String.format("%.2f", amount)}"
    
    private fun log(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "[$timestamp] $message"
        _logs.value = (_logs.value + logMessage).takeLast(50)
    }
    
    fun connectToServer(url: String, chargePointId: String) {
        if (url.isBlank()) {
            log("⚠️ Please enter a server URL")
            return
        }
        if (chargePointId.isBlank()) {
            log("⚠️ Please enter a Charger ID")
            return
        }
        
        log("🔗 Connecting to server...")
        
        try {
            connect(url, chargePointId)
        } catch (e: Exception) {
            log("❌ Connection error: ${e.message ?: "Unknown error"}")
            return
        }
        
        viewModelScope.launch {
            try {
                connectionState.collect { state ->
                    when (state) {
                        is com.ocpp.core.transport.ConnectionState.Connected -> 
                            log("✅ Connected! Ready to register charger.")
                        is com.ocpp.core.transport.ConnectionState.Error -> {
                            val errorMsg = state.exception.message ?: "Connection failed"
                            log("❌ $errorMsg")
                            log("💡 Tip: Make sure the CSMS simulator is running")
                        }
                        is com.ocpp.core.transport.ConnectionState.Reconnecting -> 
                            log("🔄 Reconnecting (${state.attempt}/${state.maxAttempts})...")
                        is com.ocpp.core.transport.ConnectionState.Disconnected ->
                            log("🔌 Disconnected from server")
                        is com.ocpp.core.transport.ConnectionState.Connecting ->
                            log("⏳ Connecting...")
                    }
                }
            } catch (e: Exception) {
                log("❌ Error: ${e.message ?: "Unknown error"}")
            }
        }
    }
    
    fun disconnectFromServer() {
        log("🔌 Disconnected")
        disconnect()
        _chargingState.value = ChargingState()
    }
    
    fun sendBootNotification(chargingType: String, powerKw: Double) {
        viewModelScope.launch {
            val chargerType = if (chargingType == "DC") "DC Fast" else "AC"
            log("📋 Registering: $chargerType ${powerKw.toInt()}kW charger")
            
            val result = bootNotification(
                model = "$chargerType Charger ${powerKw.toInt()}kW",
                vendorName = "OCPP Kotlin India",
                reason = BootReasonEnumType.PowerUp,
                firmwareVersion = "1.0.0"
            )
            
            result.onSuccess { response ->
                when (response.status) {
                    RegistrationStatusEnumType.Accepted -> 
                        log("✅ Charger registered! Ready for customers.")
                    RegistrationStatusEnumType.Pending -> 
                        log("⏳ Registration pending...")
                    RegistrationStatusEnumType.Rejected -> 
                        log("❌ Registration rejected")
                }
            }.onFailure { log("❌ Failed: ${it.message}") }
        }
    }
    
    fun sendAuthorize(idToken: String) {
        viewModelScope.launch {
            log("🔐 Authorizing: $idToken")
            
            val result = authorize(idToken)
            
            result.onSuccess { response ->
                val status = response.idTokenInfo.status
                when (status) {
                    AuthorizationStatusEnumType.Accepted -> 
                        log("✅ Authorized! Ready to charge.")
                    AuthorizationStatusEnumType.Blocked -> 
                        log("🚫 Customer is blocked")
                    AuthorizationStatusEnumType.Expired -> 
                        log("⏰ Authorization expired")
                    else -> log("⚠️ Status: $status")
                }
            }.onFailure { log("❌ Failed: ${it.message}") }
        }
    }
    
    fun sendStartTransaction(idToken: String, evseId: Int, connectorId: Int) {
        viewModelScope.launch {
            val txId = "TXN-${System.currentTimeMillis().toString().takeLast(6)}"
            currentEvseId = evseId
            currentConnectorId = connectorId
            sessionEnergyKwh = 0.0
            sessionStartTime = System.currentTimeMillis()
            
            log("⚡ Starting charging session...")
            
            val result = startTransaction(
                transactionId = txId,
                evseId = evseId,
                connectorId = connectorId,
                idToken = idToken,
                timestamp = Instant.now().toString()
            )
            
            result.onSuccess {
                _chargingState.value = ChargingState(
                    isCharging = true,
                    transactionId = txId,
                    energyKwh = 0.0,
                    durationMinutes = 0
                )
                log("🔋 Charging started! Transaction: $txId")
            }.onFailure { log("❌ Failed: ${it.message}") }
        }
    }
    
    fun sendStopTransaction() {
        val txId = _chargingState.value.transactionId ?: return
        
        viewModelScope.launch {
            log("🛑 Stopping charging...")
            
            val result = stopTransaction(
                transactionId = txId,
                timestamp = Instant.now().toString(),
                stoppedReason = ReasonEnumType.Local,
                meterValue = sessionEnergyKwh,
                seqNo = 1
            )
            
            result.onSuccess {
                val cost = sessionEnergyKwh * 12
                val duration = ((System.currentTimeMillis() - sessionStartTime) / 60000).toInt()
                
                log("✅ Charging complete!")
                log("🔋 Energy: ${String.format("%.2f", sessionEnergyKwh)} kWh")
                log("💰 Cost: ${formatCurrency(cost)}")
                
                // Add to history
                val session = ChargingSession(
                    id = txId,
                    date = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date()),
                    duration = "${duration}m",
                    energyKwh = sessionEnergyKwh,
                    cost = cost
                )
                _sessions.value = listOf(session) + _sessions.value
                
                _chargingState.value = ChargingState()
                sessionEnergyKwh = 0.0
            }.onFailure { log("❌ Failed: ${it.message}") }
        }
    }
    
    fun sendMeterValuesUpdate(evseId: Int, powerKw: Double) {
        if (!_chargingState.value.isCharging) return
        
        viewModelScope.launch {
            // Simulate 1 minute of charging
            sessionEnergyKwh += powerKw / 60.0
            val duration = ((System.currentTimeMillis() - sessionStartTime) / 60000).toInt()
            
            _chargingState.value = _chargingState.value.copy(
                energyKwh = sessionEnergyKwh,
                durationMinutes = duration
            )
            
            log("📊 Meter: ${String.format("%.2f", sessionEnergyKwh)} kWh | ${formatCurrency(sessionEnergyKwh * 12)}")
            
            val result = sendMeterValues(
                evseId = evseId,
                timestamp = Instant.now().toString(),
                energyValue = sessionEnergyKwh * 1000,
                powerValue = powerKw * 1000
            )
            
            result.onSuccess { log("✅ Meter values sent") }
                .onFailure { log("❌ Meter failed: ${it.message}") }
        }
    }
}
