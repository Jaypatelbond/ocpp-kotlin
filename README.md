# OCPP Kotlin

[![](https://jitpack.io/v/jaypatelbond/OCPPKotlin.svg)](https://jitpack.io/#jaypatelbond/OCPPKotlin)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org/)

A comprehensive, type-safe **OCPP (Open Charge Point Protocol)** client library for Kotlin and Android. Built for EV charging station developers who need reliable, production-ready OCPP communication.

## ✨ Features

- 🔌 **OCPP 2.0.1** - Full support for all 47+ message types across 15 functional blocks
- 🔌 **OCPP 1.6** - Core, Smart Charging, Firmware Management profiles
- 📱 **Android-First** - Lifecycle-aware components, ViewModel support
- 🔒 **Type-Safe** - Compile-time safety with Kotlin data classes and enums
- ⚡ **Coroutines** - Modern async/await patterns with Kotlin Coroutines and Flow
- 🔄 **Auto-Reconnect** - Automatic reconnection with exponential backoff
- 🔐 **Secure** - Basic auth and certificate-based authentication support

## 📸 Screenshots

| Home | Charging | History | Settings |
|:----:|:--------:|:-------:|:--------:|
| ![Home](screenshots/home.png) | ![Charging](screenshots/charging.png) | ![History](screenshots/history.png) | ![Settings](screenshots/settings.png) |

## 📦 Installation

Add JitPack repository to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependencies to your module's `build.gradle.kts`:

```kotlin
dependencies {
    // Core library (required)
    implementation("com.github.Jaypatelbond.ocpp-kotlin:ocpp-core:1.1.0")
    
    // OCPP 2.0.1 support
    implementation("com.github.Jaypatelbond.ocpp-kotlin:ocpp-2.0.1:1.1.0")
    
    // OCPP 1.6 support (optional)
    implementation("com.github.Jaypatelbond.ocpp-kotlin:ocpp-1.6:1.1.0")
    
    // Android extensions (optional, for Android apps)
    implementation("com.github.Jaypatelbond.ocpp-kotlin:ocpp-android:1.1.0")
}
```

## 🚀 Quick Start

### Basic Connection (OCPP 2.0.1)

```kotlin
import com.ocpp.v201.client.Ocpp201Client
import com.ocpp.v201.types.*

// Create client
val client = Ocpp201Client()

// Connect to CSMS
client.connect("ws://csms.example.com/ocpp", "CP001")

// Send BootNotification
val response = client.bootNotification(
    chargingStation = ChargingStationType(
        model = "FastCharger",
        vendorName = "MyCompany"
    ),
    reason = BootReasonEnumType.PowerUp
)

when (response.status) {
    RegistrationStatusEnumType.Accepted -> println("Charger registered!")
    RegistrationStatusEnumType.Pending -> println("Registration pending...")
    RegistrationStatusEnumType.Rejected -> println("Registration rejected")
}
```

### Android ViewModel Integration

```kotlin
class ChargingViewModel : Ocpp201ViewModel() {
    
    fun startCharging(customerId: String) {
        viewModelScope.launch {
            // Authorize customer
            val authResult = authorize(customerId)
            if (authResult.isSuccess) {
                // Start transaction
                startTransaction(
                    transactionId = UUID.randomUUID().toString(),
                    evseId = 1,
                    connectorId = 1,
                    idToken = customerId,
                    timestamp = Instant.now().toString()
                )
            }
        }
    }
}
```

### 🧩 Generic API (Version Agnostic)

Write code that works with both OCPP 1.6 and 2.0.1:

```kotlin
// Select adapter
val client: GenericOcppClient = when (config.version) {
    "1.6" -> GenericOcpp16Adapter()
    "2.0.1" -> GenericOcpp201Adapter()
    else -> throw IllegalArgumentException("Unknown version")
}

// Connect & Authenticate (works for both!)
client.connect(url, chargePointId)
val bootParams = client.bootNotification("MyCharger", "MyCompany").getOrThrow()

if (bootParams.accepted) {
    client.startTransaction(connectorId = 1, idToken = "TAG123", meterStart = 0)
}
```

### Complete Transaction Flow

```kotlin
// 1. Boot & Register
client.bootNotification(station, BootReasonEnumType.PowerUp)

// 2. Report Available
client.statusNotification(timestamp, ConnectorStatusEnumType.Available, evseId = 1, connectorId = 1)

// 3. Authorize Customer
val auth = client.authorize(idToken).getOrThrow()

// 4. Start Transaction
client.transactionEvent(TransactionEventEnumType.Started, timestamp, TriggerReasonEnumType.Authorized, seqNo = 0, transactionInfo)

// 5. Charging - Send Meter Values
client.transactionEvent(TransactionEventEnumType.Updated, timestamp, TriggerReasonEnumType.MeterValuePeriodic, seqNo = 1, transactionInfo, meterValue = meterValues)

// 6. Stop Transaction
client.transactionEvent(TransactionEventEnumType.Ended, timestamp, TriggerReasonEnumType.Local, seqNo = 2, transactionInfo)
```

📖 **[See Full Transaction Examples →](docs/TRANSACTION_FLOW.md)**

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Your Application                          │
├─────────────────────────────────────────────────────────────────┤
│  ocpp-android     │  Ocpp201ViewModel, LifecycleAwareClient    │
├───────────────────┼─────────────────────────────────────────────┤
│  ocpp-2.0.1       │  Ocpp201Client, Messages, Types             │
│  ocpp-1.6         │  Ocpp16Client, Messages, Types              │
├───────────────────┼─────────────────────────────────────────────┤
│  ocpp-core        │  OcppClient, Transport, Message Parser      │
├───────────────────┼─────────────────────────────────────────────┤
│  OkHttp           │  WebSocket Transport Layer                   │
└─────────────────────────────────────────────────────────────────┘
```

## 📚 Modules

| Module | Description |
|--------|-------------|
| `ocpp-core` | Base transport, message parsing, request/response correlation |
| `ocpp-2.0.1` | OCPP 2.0.1 messages, types, and type-safe client |
| `ocpp-1.6` | OCPP 1.6 messages, types, and type-safe client |
| `ocpp-android` | Android lifecycle integration, ViewModel base classes |
| `sample-app` | Demo Android app with charging simulation |
| `ocpp-simulator` | CSMS simulator for testing |

## 🔌 Supported OCPP 2.0.1 Messages

### Charging Station → CSMS
- BootNotification, Heartbeat, StatusNotification
- Authorize, TransactionEvent, MeterValues
- FirmwareStatusNotification, LogStatusNotification
- SecurityEventNotification, DataTransfer
- And more...

### CSMS → Charging Station
- RequestStartTransaction, RequestStopTransaction
- SetVariables, GetVariables, GetBaseReport
- SetChargingProfile, ClearChargingProfile
- TriggerMessage, Reset, UnlockConnector
- And more...

## 🧪 Testing with Simulator

Run the included OCPP CSMS simulator for testing:

```bash
./gradlew :ocpp-simulator:run
```

Then run the sample app on an Android emulator and connect to:
- Emulator: `ws://10.0.2.2:8080/ocpp`
- Physical device: `ws://YOUR_PC_IP:8080/ocpp`

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Open Charge Alliance](https://www.openchargealliance.org/) for the OCPP specification
- [OkHttp](https://square.github.io/okhttp/) for WebSocket support
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON handling
