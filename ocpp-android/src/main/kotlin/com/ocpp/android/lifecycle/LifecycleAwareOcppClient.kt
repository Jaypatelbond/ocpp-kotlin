package com.ocpp.android.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ocpp.core.client.OcppClient
import com.ocpp.core.transport.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Lifecycle-aware wrapper for OcppClient that automatically manages connection
 * based on Android lifecycle events.
 *
 * When the lifecycle reaches STARTED state, it will reconnect if previously connected.
 * When the lifecycle reaches STOPPED state, it will disconnect.
 *
 * Example usage:
 * ```kotlin
 * class MainActivity : AppCompatActivity() {
 *     private val ocppClient = Ocpp201Client()
 *     private lateinit var lifecycleClient: LifecycleAwareOcppClient
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         lifecycleClient = LifecycleAwareOcppClient(
 *             client = ocppClient,
 *             lifecycle = lifecycle,
 *             url = "ws://csms.example.com/ocpp",
 *             chargePointId = "CP001"
 *         )
 *     }
 * }
 * ```
 */
class LifecycleAwareOcppClient(
    private val client: OcppClient,
    lifecycle: Lifecycle,
    private val url: String,
    private val chargePointId: String
) : DefaultLifecycleObserver {
    
    private var wasConnected = false
    private var scope: CoroutineScope? = null
    
    /**
     * Current connection state.
     */
    val connectionState: StateFlow<ConnectionState> = client.connectionState
    
    init {
        lifecycle.addObserver(this)
    }
    
    override fun onStart(owner: LifecycleOwner) {
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        
        if (wasConnected) {
            scope?.launch {
                try {
                    client.connect(url, chargePointId)
                } catch (e: Exception) {
                    // Connection failed, will be handled by connectionState
                }
            }
        }
    }
    
    override fun onStop(owner: LifecycleOwner) {
        wasConnected = client.connectionState.value == ConnectionState.Connected
        
        scope?.launch {
            client.disconnect()
        }
        scope?.cancel()
        scope = null
    }
    
    /**
     * Manually initiate connection.
     * Called automatically on lifecycle start if was previously connected.
     */
    fun connect() {
        scope?.launch {
            client.connect(url, chargePointId)
            wasConnected = true
        }
    }
    
    /**
     * Manually disconnect.
     */
    fun disconnect() {
        scope?.launch {
            client.disconnect()
            wasConnected = false
        }
    }
    
    /**
     * Get the underlying OcppClient for direct access.
     */
    fun getClient(): OcppClient = client
}

/**
 * Extension function to make any OcppClient lifecycle-aware.
 */
fun OcppClient.withLifecycle(
    lifecycle: Lifecycle,
    url: String,
    chargePointId: String
): LifecycleAwareOcppClient {
    return LifecycleAwareOcppClient(this, lifecycle, url, chargePointId)
}
