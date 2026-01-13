package com.ocpp.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ocpp.sample.ui.BottomNavBar
import com.ocpp.sample.ui.Screen
import com.ocpp.sample.ui.screens.*
import com.ocpp.sample.ui.theme.OCPPKotlinTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OCPPKotlinTheme {
                OcppApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcppApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val chargingState by viewModel.chargingState.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    
    // Settings state
    var url by remember { mutableStateOf("ws://10.0.2.2:8080/ocpp") }
    var chargePointId by remember { mutableStateOf("CP001") }
    var idToken by remember { mutableStateOf("RFID123456") }
    var powerKw by remember { mutableStateOf("7.4") }
    var isAcCharging by remember { mutableStateOf(true) }
    
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    connectionState = connectionState,
                    chargingState = chargingState,
                    url = url,
                    onUrlChange = { url = it },
                    chargePointId = chargePointId,
                    onChargePointIdChange = { chargePointId = it },
                    idToken = idToken,
                    onIdTokenChange = { idToken = it },
                    powerKw = powerKw,
                    onPowerKwChange = { powerKw = it },
                    isAcCharging = isAcCharging,
                    onChargingTypeChange = { 
                        isAcCharging = it
                        powerKw = if (it) "7.4" else "50"
                    },
                    onConnect = { viewModel.connectToServer(url, chargePointId) },
                    onDisconnect = { viewModel.disconnectFromServer() },
                    onBootNotification = { 
                        viewModel.sendBootNotification(
                            if (isAcCharging) "AC" else "DC",
                            powerKw.toDoubleOrNull() ?: 7.4
                        )
                    },
                    onAuthorize = { viewModel.sendAuthorize(idToken) },
                    onStartCharging = { 
                        viewModel.sendStartTransaction(idToken, 1, 1)
                        navController.navigate(Screen.Charging.route)
                    },
                    onNavigateToCharging = { navController.navigate(Screen.Charging.route) }
                )
            }
            
            composable(Screen.Charging.route) {
                ChargingScreen(
                    chargingState = chargingState,
                    powerKw = powerKw.toFloatOrNull() ?: 7.4f,
                    onSendMeterValues = { 
                        viewModel.sendMeterValuesUpdate(1, powerKw.toDoubleOrNull() ?: 7.4)
                    },
                    onStopCharging = {
                        viewModel.sendStopTransaction()
                        navController.navigate(Screen.Home.route)
                    }
                )
            }
            
            composable(Screen.History.route) {
                HistoryScreen(sessions = sessions)
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(logs = logs)
            }
        }
    }
}
