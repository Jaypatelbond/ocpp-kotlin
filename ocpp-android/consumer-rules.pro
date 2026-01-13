# Consumer proguard rules for ocpp-android library
# These rules will be included in the consuming app

# Keep OCPP message classes for serialization
-keep class com.ocpp.v201.messages.** { *; }
-keep class com.ocpp.v201.types.** { *; }
-keep class com.ocpp.v16.messages.** { *; }
-keep class com.ocpp.v16.types.** { *; }
-keep class com.ocpp.core.message.** { *; }
