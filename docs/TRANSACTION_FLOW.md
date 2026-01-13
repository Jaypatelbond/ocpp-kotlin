# OCPP Transaction Flow Guide

This guide walks you through implementing a complete EV charging transaction using the OCPP Kotlin library.

## Overview

A typical charging transaction follows this flow:

```
┌──────────────────────────────────────────────────────────────────┐
│                    CHARGING TRANSACTION FLOW                      │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  1. Boot & Register     →  BootNotification                      │
│  2. Report Status       →  StatusNotification (Available)        │
│  3. Customer Arrives    →  StatusNotification (Preparing)        │
│  4. Authorize           →  Authorize (RFID/App)                  │
│  5. Start Transaction   →  TransactionEvent (Started)            │
│  6. Charging            →  StatusNotification (Charging)         │
│  7. Send Meter Values   →  MeterValues / TransactionEvent        │
│  8. Stop Transaction    →  TransactionEvent (Ended)              │
│  9. Report Status       →  StatusNotification (Available)        │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## OCPP 2.0.1 Transaction Example

```kotlin
import com.ocpp.v201.client.Ocpp201Client
import com.ocpp.v201.types.*
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.UUID

fun main() = runBlocking {
    val client = Ocpp201Client()
    
    // Step 1: Connect to CSMS
    client.connect("ws://csms.example.com/ocpp", "CP001")
    
    // Step 2: Boot Notification - Register the charging station
    val bootResponse = client.bootNotification(
        chargingStation = ChargingStationType(
            model = "FastCharger Pro",
            vendorName = "OCPP Kotlin"
        ),
        reason = BootReasonEnumType.PowerUp
    ).getOrThrow()
    
    if (bootResponse.status != RegistrationStatusEnumType.Accepted) {
        println("Registration failed: ${bootResponse.status}")
        return@runBlocking
    }
    println("✅ Registered! Heartbeat interval: ${bootResponse.interval}s")
    
    // Step 3: Report connector as available
    client.statusNotification(
        timestamp = Instant.now().toString(),
        connectorStatus = ConnectorStatusEnumType.Available,
        evseId = 1,
        connectorId = 1
    )
    
    // Step 4: Customer arrives - Authorize their ID
    val idToken = IdTokenType(
        idToken = "RFID123456",
        type = IdTokenEnumType.ISO14443
    )
    
    val authResponse = client.authorize(idToken).getOrThrow()
    
    if (authResponse.idTokenInfo.status != AuthorizationStatusEnumType.Accepted) {
        println("❌ Authorization failed")
        return@runBlocking
    }
    println("✅ Customer authorized!")
    
    // Step 5: Start the transaction
    val transactionId = UUID.randomUUID().toString()
    
    client.transactionEvent(
        eventType = TransactionEventEnumType.Started,
        timestamp = Instant.now().toString(),
        triggerReason = TriggerReasonEnumType.Authorized,
        seqNo = 0,
        transactionInfo = TransactionType(transactionId = transactionId),
        evse = EVSEType(id = 1, connectorId = 1),
        idToken = idToken
    )
    println("⚡ Transaction started: $transactionId")
    
    // Step 6: Report charging status
    client.statusNotification(
        timestamp = Instant.now().toString(),
        connectorStatus = ConnectorStatusEnumType.Occupied,
        evseId = 1,
        connectorId = 1
    )
    
    // Step 7: Send meter values during charging
    val meterValue = MeterValueType(
        timestamp = Instant.now().toString(),
        sampledValue = listOf(
            SampledValueType(
                value = 15.5, // 15.5 kWh delivered
                measurand = MeasurandEnumType.Energy_Active_Import_Register,
                unitOfMeasure = UnitOfMeasureType(unit = "kWh")
            ),
            SampledValueType(
                value = 7.4, // 7.4 kW power
                measurand = MeasurandEnumType.Power_Active_Import
            )
        )
    )
    
    client.transactionEvent(
        eventType = TransactionEventEnumType.Updated,
        timestamp = Instant.now().toString(),
        triggerReason = TriggerReasonEnumType.MeterValuePeriodic,
        seqNo = 1,
        transactionInfo = TransactionType(transactionId = transactionId),
        meterValue = listOf(meterValue)
    )
    println("📊 Meter values sent: 15.5 kWh")
    
    // Step 8: Stop the transaction
    client.transactionEvent(
        eventType = TransactionEventEnumType.Ended,
        timestamp = Instant.now().toString(),
        triggerReason = TriggerReasonEnumType.StopAuthorized,
        seqNo = 2,
        transactionInfo = TransactionType(
            transactionId = transactionId,
            stoppedReason = ReasonEnumType.Local
        ),
        idToken = idToken
    )
    println("🛑 Transaction ended")
    
    // Step 9: Report available again
    client.statusNotification(
        timestamp = Instant.now().toString(),
        connectorStatus = ConnectorStatusEnumType.Available,
        evseId = 1,
        connectorId = 1
    )
    
    client.disconnect()
    println("✅ Complete!")
}
```

---

## OCPP 1.6 Transaction Example

```kotlin
import com.ocpp.v16.client.Ocpp16Client
import com.ocpp.v16.types.*
import kotlinx.coroutines.runBlocking
import java.time.Instant

fun main() = runBlocking {
    val client = Ocpp16Client()
    
    // Connect
    client.connect("ws://csms.example.com/ocpp/1.6", "CP001")
    
    // Boot Notification
    val bootResponse = client.bootNotification(
        chargePointModel = "AC Charger",
        chargePointVendor = "OCPP Kotlin"
    ).getOrThrow()
    
    if (bootResponse.status != RegistrationStatus.Accepted) {
        println("Registration failed")
        return@runBlocking
    }
    
    // Report Available
    client.statusNotification(
        connectorId = 1,
        status = ChargePointStatus.Available,
        errorCode = ChargePointErrorCode.NoError
    )
    
    // Authorize
    val idTag = "RFID123456"
    val authResponse = client.authorize(idTag).getOrThrow()
    
    if (authResponse.idTagInfo.status != AuthorizationStatus.Accepted) {
        println("Authorization failed")
        return@runBlocking
    }
    
    // Start Transaction
    val startResponse = client.startTransaction(
        connectorId = 1,
        idTag = idTag,
        meterStart = 0,
        timestamp = Instant.now().toString()
    ).getOrThrow()
    
    val transactionId = startResponse.transactionId
    println("Transaction started: $transactionId")
    
    // Update status to Charging
    client.statusNotification(
        connectorId = 1,
        status = ChargePointStatus.Charging,
        errorCode = ChargePointErrorCode.NoError
    )
    
    // Send Meter Values
    client.meterValues(
        connectorId = 1,
        meterValue = listOf(
            MeterValue(
                timestamp = Instant.now().toString(),
                sampledValue = listOf(
                    SampledValue(value = "15500", unit = "Wh")
                )
            )
        ),
        transactionId = transactionId
    )
    
    // Stop Transaction
    client.stopTransaction(
        meterStop = 15500,
        timestamp = Instant.now().toString(),
        transactionId = transactionId
    )
    
    // Back to Available
    client.statusNotification(
        connectorId = 1,
        status = ChargePointStatus.Available,
        errorCode = ChargePointErrorCode.NoError
    )
    
    client.disconnect()
}
```

---

## Handling Remote Commands

The CSMS can send commands to your charging station. Handle them like this:

```kotlin
// OCPP 2.0.1
client.onRequestStartTransaction { request ->
    println("Remote start requested for EVSE ${request.evseId}")
    
    // Start your charging logic here
    startCharging(request.evseId, request.idToken)
    
    RequestStartTransactionResponse(
        status = RequestStartStopStatusEnumType.Accepted
    )
}

client.onRequestStopTransaction { request ->
    println("Remote stop requested: ${request.transactionId}")
    
    stopCharging(request.transactionId)
    
    RequestStopTransactionResponse(
        status = RequestStartStopStatusEnumType.Accepted
    )
}

// OCPP 1.6
client.onRemoteStartTransaction { request ->
    RemoteStartTransactionResponse(status = RemoteStartStopStatus.Accepted)
}

client.onRemoteStopTransaction { request ->
    RemoteStopTransactionResponse(status = RemoteStartStopStatus.Accepted)
}
```

---

## Best Practices

1. **Always send BootNotification first** - Register before any other operations
2. **Handle heartbeats** - Send at the interval specified in BootNotificationResponse
3. **Report status changes** - Keep the CSMS informed of connector state
4. **Sequence numbers matter** - Increment seqNo for each TransactionEvent
5. **Handle reconnection** - The library auto-reconnects, but re-send BootNotification

## Error Handling

```kotlin
val response = client.authorize(idToken)

response.onSuccess { auth ->
    if (auth.idTokenInfo.status == AuthorizationStatusEnumType.Accepted) {
        // Proceed with charging
    } else {
        // Show error to customer
    }
}

response.onFailure { error ->
    when (error) {
        is OcppTimeoutException -> println("Request timed out")
        is OcppTransportException -> println("Connection issue")
        else -> println("Error: ${error.message}")
    }
}
```
