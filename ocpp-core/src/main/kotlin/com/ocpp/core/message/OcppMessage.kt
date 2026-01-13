package com.ocpp.core.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Base interface for all OCPP messages.
 * OCPP-J (JSON over WebSocket) uses a simple message format:
 * - [2, "messageId", "action", {payload}] - Call (Request)
 * - [3, "messageId", {payload}] - CallResult (Response)
 * - [4, "messageId", "errorCode", "errorDescription", {details}] - CallError
 */
sealed class OcppMessage {
    abstract val messageId: String
}

/**
 * OCPP Call message - A request from either Charge Point or CSMS.
 * Format: [2, "<messageId>", "<action>", {<payload>}]
 *
 * @property messageId Unique identifier for this message (used for correlation)
 * @property action The OCPP action name (e.g., "BootNotification", "Authorize")
 * @property payload The JSON payload containing request data
 */
data class Call(
    override val messageId: String,
    val action: String,
    val payload: JsonObject
) : OcppMessage() {
    companion object {
        const val MESSAGE_TYPE_ID = 2
    }
}

/**
 * OCPP CallResult message - A successful response to a Call.
 * Format: [3, "<messageId>", {<payload>}]
 *
 * @property messageId Must match the messageId of the corresponding Call
 * @property payload The JSON payload containing response data
 */
data class CallResult(
    override val messageId: String,
    val payload: JsonObject
) : OcppMessage() {
    companion object {
        const val MESSAGE_TYPE_ID = 3
    }
}

/**
 * OCPP CallError message - An error response to a Call.
 * Format: [4, "<messageId>", "<errorCode>", "<errorDescription>", {<errorDetails>}]
 *
 * @property messageId Must match the messageId of the corresponding Call
 * @property errorCode The error code as defined in OCPP specification
 * @property errorDescription Human-readable error description
 * @property errorDetails Optional additional error details
 */
data class CallError(
    override val messageId: String,
    val errorCode: ErrorCode,
    val errorDescription: String,
    val errorDetails: JsonObject? = null
) : OcppMessage() {
    companion object {
        const val MESSAGE_TYPE_ID = 4
    }
}

/**
 * OCPP-J Error Codes as defined in the specification.
 */
@Serializable
enum class ErrorCode {
    /** Payload is syntactically incorrect */
    FormatViolation,
    
    /** Any other error not covered by specific error codes */
    GenericError,
    
    /** An internal error occurred */
    InternalError,
    
    /** Message type is not supported */
    MessageTypeNotSupported,
    
    /** Requested action is recognized but not implemented */
    NotImplemented,
    
    /** Requested action is not supported */
    NotSupported,
    
    /** Too many occurrences of an element */
    OccurrenceConstraintViolation,
    
    /** Property constraint violation (e.g., maxLength) */
    PropertyConstraintViolation,
    
    /** Protocol error in the OCPP-J transport layer */
    ProtocolError,
    
    /** RPC framework error */
    RpcFrameworkError,
    
    /** Security error during message processing */
    SecurityError,
    
    /** Type constraint violation (e.g., wrong data type) */
    TypeConstraintViolation
}
