pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "OCPP Kotlin"

// Core module - WebSocket transport, base message types
include(":ocpp-core")

// OCPP Version implementations
include(":ocpp-1.6")
include(":ocpp-2.0.1")

// Android-specific extensions
include(":ocpp-android")

// Sample application
include(":sample-app")

// CSMS Simulator for testing
include(":ocpp-simulator")
