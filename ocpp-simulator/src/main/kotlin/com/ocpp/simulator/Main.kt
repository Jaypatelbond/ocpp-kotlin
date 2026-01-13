package com.ocpp.simulator

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.*
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * Simple OCPP 2.0.1 Central System (CSMS) Simulator
 * 
 * Supports basic OCPP operations for testing:
 * - BootNotification
 * - Heartbeat
 * - Authorize
 * - TransactionEvent
 * - StatusNotification
 * - MeterValues
 */

val json = Json { 
    ignoreUnknownKeys = true 
    prettyPrint = true
}

// Store connected charge points
val connectedChargePoints = ConcurrentHashMap<String, WebSocketSession>()

// Store active transactions
val activeTransactions = ConcurrentHashMap<String, TransactionInfo>()

data class TransactionInfo(
    val transactionId: String,
    val chargePointId: String,
    val evseId: Int,
    val idToken: String,
    val startTime: Instant,
    var meterStart: Double = 0.0
)

fun main() {
    println("""
        ╔═══════════════════════════════════════════════════════════╗
        ║         OCPP 2.0.1 CSMS Simulator                         ║
        ║                                                           ║
        ║  WebSocket URL: ws://localhost:8080/ocpp/{chargePointId}  ║
        ║                                                           ║
        ║  Supported Messages:                                      ║
        ║  • BootNotification                                       ║
        ║  • Heartbeat                                              ║
        ║  • Authorize                                               ║
        ║  • TransactionEvent                                       ║
        ║  • StatusNotification                                     ║
        ║  • MeterValues                                            ║
        ║  • DataTransfer                                           ║
        ╚═══════════════════════════════════════════════════════════╝
    """.trimIndent())
    
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(30)
            timeout = Duration.ofSeconds(60)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        
        routing {
            webSocket("/ocpp/{chargePointId}") {
                val chargePointId = call.parameters["chargePointId"] ?: "unknown"
                
                println("\n[CONNECT] Charge Point '$chargePointId' connected")
                connectedChargePoints[chargePointId] = this
                
                try {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            println("\n[RECV] $chargePointId: $text")
                            
                            val response = handleOcppMessage(chargePointId, text)
                            if (response != null) {
                                println("[SEND] $chargePointId: $response")
                                send(Frame.Text(response))
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("[ERROR] $chargePointId: ${e.message}")
                } finally {
                    connectedChargePoints.remove(chargePointId)
                    println("[DISCONNECT] Charge Point '$chargePointId' disconnected")
                }
            }
        }
    }.start(wait = true)
}

fun handleOcppMessage(chargePointId: String, message: String): String? {
    return try {
        val jsonArray = json.parseToJsonElement(message).jsonArray
        val messageTypeId = jsonArray[0].jsonPrimitive.int
        
        when (messageTypeId) {
            2 -> handleCall(chargePointId, jsonArray) // Call
            3 -> { 
                println("  → CallResult received") 
                null 
            }
            4 -> { 
                println("  → CallError received") 
                null 
            }
            else -> null
        }
    } catch (e: Exception) {
        println("  → Error parsing message: ${e.message}")
        null
    }
}

fun handleCall(chargePointId: String, jsonArray: JsonArray): String {
    val messageId = jsonArray[1].jsonPrimitive.content
    val action = jsonArray[2].jsonPrimitive.content
    val payload = jsonArray[3].jsonObject
    
    println("  → Action: $action")
    
    val responsePayload = when (action) {
        "BootNotification" -> handleBootNotification(payload)
        "Heartbeat" -> handleHeartbeat()
        "Authorize" -> handleAuthorize(payload)
        "TransactionEvent" -> handleTransactionEvent(chargePointId, payload)
        "StatusNotification" -> handleStatusNotification(payload)
        "MeterValues" -> handleMeterValues(payload)
        "DataTransfer" -> handleDataTransfer(payload)
        "FirmwareStatusNotification" -> handleFirmwareStatusNotification(payload)
        "LogStatusNotification" -> handleLogStatusNotification(payload)
        "SecurityEventNotification" -> handleSecurityEventNotification(payload)
        else -> {
            println("  → Unknown action: $action")
            buildJsonObject { put("status", "NotImplemented") }
        }
    }
    
    // Return CallResult [3, messageId, payload]
    return buildJsonArray {
        add(3)
        add(messageId)
        add(responsePayload)
    }.toString()
}

fun handleBootNotification(payload: JsonObject): JsonObject {
    val vendor = payload["chargingStation"]?.jsonObject?.get("vendorName")?.jsonPrimitive?.content ?: "Unknown"
    val model = payload["chargingStation"]?.jsonObject?.get("model")?.jsonPrimitive?.content ?: "Unknown"
    val reason = payload["reason"]?.jsonPrimitive?.content ?: "Unknown"
    
    println("  → Vendor: $vendor, Model: $model, Reason: $reason")
    
    return buildJsonObject {
        put("currentTime", Instant.now().toString())
        put("interval", 300) // 5 minute heartbeat
        put("status", "Accepted")
    }
}

fun handleHeartbeat(): JsonObject {
    return buildJsonObject {
        put("currentTime", Instant.now().toString())
    }
}

fun handleAuthorize(payload: JsonObject): JsonObject {
    val idToken = payload["idToken"]?.jsonObject?.get("idToken")?.jsonPrimitive?.content ?: "Unknown"
    val tokenType = payload["idToken"]?.jsonObject?.get("type")?.jsonPrimitive?.content ?: "Unknown"
    
    println("  → Token: $idToken, Type: $tokenType")
    
    // Accept all tokens for testing
    return buildJsonObject {
        putJsonObject("idTokenInfo") {
            put("status", "Accepted")
            put("cacheExpiryDateTime", Instant.now().plusSeconds(86400).toString())
        }
    }
}

fun handleTransactionEvent(chargePointId: String, payload: JsonObject): JsonObject {
    val eventType = payload["eventType"]?.jsonPrimitive?.content ?: "Unknown"
    val transactionId = payload["transactionInfo"]?.jsonObject?.get("transactionId")?.jsonPrimitive?.content ?: "Unknown"
    val idToken = payload["idToken"]?.jsonObject?.get("idToken")?.jsonPrimitive?.content
    
    println("  → Event: $eventType, Transaction: $transactionId")
    
    when (eventType) {
        "Started" -> {
            val evseId = payload["evse"]?.jsonObject?.get("id")?.jsonPrimitive?.int ?: 1
            activeTransactions[transactionId] = TransactionInfo(
                transactionId = transactionId,
                chargePointId = chargePointId,
                evseId = evseId,
                idToken = idToken ?: "Unknown",
                startTime = Instant.now()
            )
            println("  → Transaction STARTED")
        }
        "Updated" -> {
            println("  → Transaction UPDATED")
        }
        "Ended" -> {
            activeTransactions.remove(transactionId)
            println("  → Transaction ENDED")
            
            // Return with totalCost in INR (₹12/kWh typical rate)
            return buildJsonObject {
                put("totalCost", 156.50) // Sample cost in INR
                putJsonObject("idTokenInfo") {
                    put("status", "Accepted")
                }
            }
        }
    }
    
    return buildJsonObject {
        idToken?.let {
            putJsonObject("idTokenInfo") {
                put("status", "Accepted")
            }
        }
    }
}

fun handleStatusNotification(payload: JsonObject): JsonObject {
    val connectorStatus = payload["connectorStatus"]?.jsonPrimitive?.content ?: "Unknown"
    val evseId = payload["evseId"]?.jsonPrimitive?.int ?: 0
    val connectorId = payload["connectorId"]?.jsonPrimitive?.int ?: 0
    
    println("  → EVSE: $evseId, Connector: $connectorId, Status: $connectorStatus")
    
    return buildJsonObject { }
}

fun handleMeterValues(payload: JsonObject): JsonObject {
    val evseId = payload["evseId"]?.jsonPrimitive?.int ?: 0
    val meterValues = payload["meterValue"]?.jsonArray
    
    meterValues?.forEach { mv ->
        val timestamp = mv.jsonObject["timestamp"]?.jsonPrimitive?.content
        val sampledValues = mv.jsonObject["sampledValue"]?.jsonArray
        
        sampledValues?.forEach { sv ->
            val value = sv.jsonObject["value"]?.jsonPrimitive?.double ?: 0.0
            val measurand = sv.jsonObject["measurand"]?.jsonPrimitive?.content ?: "Unknown"
            println("  → EVSE: $evseId, $measurand: $value")
        }
    }
    
    return buildJsonObject { }
}

fun handleDataTransfer(payload: JsonObject): JsonObject {
    val vendorId = payload["vendorId"]?.jsonPrimitive?.content ?: "Unknown"
    val messageId = payload["messageId"]?.jsonPrimitive?.content
    val data = payload["data"]?.jsonPrimitive?.content
    
    println("  → Vendor: $vendorId, MessageId: $messageId")
    data?.let { println("  → Data: $it") }
    
    return buildJsonObject {
        put("status", "Accepted")
        put("data", "Echo: ${data ?: "no data"}")
    }
}

fun handleFirmwareStatusNotification(payload: JsonObject): JsonObject {
    val status = payload["status"]?.jsonPrimitive?.content ?: "Unknown"
    println("  → Firmware Status: $status")
    return buildJsonObject { }
}

fun handleLogStatusNotification(payload: JsonObject): JsonObject {
    val status = payload["status"]?.jsonPrimitive?.content ?: "Unknown"
    println("  → Log Status: $status")
    return buildJsonObject { }
}

fun handleSecurityEventNotification(payload: JsonObject): JsonObject {
    val type = payload["type"]?.jsonPrimitive?.content ?: "Unknown"
    println("  → Security Event: $type")
    return buildJsonObject { }
}
