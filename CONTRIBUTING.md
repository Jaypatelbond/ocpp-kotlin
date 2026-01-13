# Contributing to OCPP Kotlin

Thank you for your interest in contributing to OCPP Kotlin! This document provides guidelines for contributing to the project.

## 🚀 Getting Started

1. **Fork** the repository
2. **Clone** your fork: `git clone https://github.com/YOUR_USERNAME/OCPPKotlin.git`
3. **Create a branch**: `git checkout -b feature/your-feature-name`

## 💻 Development Setup

### Requirements
- JDK 17 or higher
- Android Studio Hedgehog (2023.1.1) or newer
- Kotlin 1.9+

### Building
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Running the Sample App
```bash
# Start the CSMS simulator
./gradlew :ocpp-simulator:run

# In Android Studio, run the sample-app module
```

## 📝 Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for all public APIs
- Keep functions small and focused

### Example KDoc

```kotlin
/**
 * Sends a BootNotification to the CSMS.
 *
 * This should be the first message sent after connecting to register
 * the charging station with the central system.
 *
 * @param chargingStation Information about the charging station
 * @param reason The reason for sending this boot notification
 * @return The response from the CSMS containing registration status
 * @throws OcppTransportException if not connected
 */
suspend fun bootNotification(
    chargingStation: ChargingStationType,
    reason: BootReasonEnumType
): BootNotificationResponse
```

## 🔀 Pull Request Process

1. Ensure your code builds without errors: `./gradlew build`
2. Add tests for new functionality
3. Update documentation if needed
4. Create a Pull Request with a clear description

### PR Title Format
- `feat: Add new feature`
- `fix: Fix bug description`
- `docs: Update documentation`
- `refactor: Refactor code`

## 🐛 Reporting Issues

When reporting issues, please include:
- OCPP version (1.6 or 2.0.1)
- Library version
- Android/Kotlin version
- Steps to reproduce
- Expected vs actual behavior

## 📋 Areas for Contribution

- Add unit tests
- Improve documentation
- Add missing OCPP message types
- Performance optimizations
- Bug fixes

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.
