package com.ocpp.core.message

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Parser for OCPP-J message format.
 * Handles serialization/deserialization of OCPP messages to/from JSON arrays.
 */
object OcppMessageParser {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    
    /**
     * Parse a raw JSON string into an OcppMessage.
     *
     * @param rawMessage The raw JSON message string
     * @return The parsed OcppMessage (Call, CallResult, or CallError)
     * @throws OcppParseException if the message format is invalid
     */
    fun parse(rawMessage: String): OcppMessage {
        return try {
            val jsonArray = json.parseToJsonElement(rawMessage).jsonArray
            
            val messageTypeId = jsonArray[0].jsonPrimitive.int
            val messageId = jsonArray[1].jsonPrimitive.content
            
            when (messageTypeId) {
                Call.MESSAGE_TYPE_ID -> parseCall(messageId, jsonArray)
                CallResult.MESSAGE_TYPE_ID -> parseCallResult(messageId, jsonArray)
                CallError.MESSAGE_TYPE_ID -> parseCallError(messageId, jsonArray)
                else -> throw OcppParseException("Unknown message type ID: $messageTypeId")
            }
        } catch (e: OcppParseException) {
            throw e
        } catch (e: Exception) {
            throw OcppParseException("Failed to parse OCPP message: ${e.message}", e)
        }
    }
    
    private fun parseCall(messageId: String, jsonArray: JsonArray): Call {
        if (jsonArray.size < 4) {
            throw OcppParseException("Call message requires at least 4 elements")
        }
        
        val action = jsonArray[2].jsonPrimitive.content
        val payload = jsonArray[3].jsonObject
        
        return Call(messageId, action, payload)
    }
    
    private fun parseCallResult(messageId: String, jsonArray: JsonArray): CallResult {
        if (jsonArray.size < 3) {
            throw OcppParseException("CallResult message requires at least 3 elements")
        }
        
        val payload = jsonArray[2].jsonObject
        
        return CallResult(messageId, payload)
    }
    
    private fun parseCallError(messageId: String, jsonArray: JsonArray): CallError {
        if (jsonArray.size < 5) {
            throw OcppParseException("CallError message requires 5 elements")
        }
        
        val errorCodeString = jsonArray[2].jsonPrimitive.content
        val errorCode = try {
            ErrorCode.valueOf(errorCodeString)
        } catch (e: IllegalArgumentException) {
            ErrorCode.GenericError
        }
        
        val errorDescription = jsonArray[3].jsonPrimitive.content
        val errorDetails = jsonArray.getOrNull(4)?.jsonObject
        
        return CallError(messageId, errorCode, errorDescription, errorDetails)
    }
    
    /**
     * Serialize an OcppMessage to its JSON string representation.
     *
     * @param message The OcppMessage to serialize
     * @return The JSON string representation
     */
    fun serialize(message: OcppMessage): String {
        val jsonArray = when (message) {
            is Call -> buildCallArray(message)
            is CallResult -> buildCallResultArray(message)
            is CallError -> buildCallErrorArray(message)
        }
        return json.encodeToString(JsonArray.serializer(), jsonArray)
    }
    
    private fun buildCallArray(call: Call): JsonArray {
        return kotlinx.serialization.json.buildJsonArray {
            add(kotlinx.serialization.json.JsonPrimitive(Call.MESSAGE_TYPE_ID))
            add(kotlinx.serialization.json.JsonPrimitive(call.messageId))
            add(kotlinx.serialization.json.JsonPrimitive(call.action))
            add(call.payload)
        }
    }
    
    private fun buildCallResultArray(callResult: CallResult): JsonArray {
        return kotlinx.serialization.json.buildJsonArray {
            add(kotlinx.serialization.json.JsonPrimitive(CallResult.MESSAGE_TYPE_ID))
            add(kotlinx.serialization.json.JsonPrimitive(callResult.messageId))
            add(callResult.payload)
        }
    }
    
    private fun buildCallErrorArray(callError: CallError): JsonArray {
        return kotlinx.serialization.json.buildJsonArray {
            add(kotlinx.serialization.json.JsonPrimitive(CallError.MESSAGE_TYPE_ID))
            add(kotlinx.serialization.json.JsonPrimitive(callError.messageId))
            add(kotlinx.serialization.json.JsonPrimitive(callError.errorCode.name))
            add(kotlinx.serialization.json.JsonPrimitive(callError.errorDescription))
            add(callError.errorDetails ?: buildJsonObject { })
        }
    }
}

/**
 * Exception thrown when OCPP message parsing fails.
 */
class OcppParseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
