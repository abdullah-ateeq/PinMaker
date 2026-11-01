# PinMaker 🔐

<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" width="120" alt="PinMaker Logo">
</p>

<p align="center">
  <b>A secure, customizable, and modern Android PIN & Password generator built with Jetpack Compose and Material 3.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Kotlin-2.0%2B-blue.svg" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-orange.svg" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/MinSDK-26%20%2F%20TargetSDK-37-blueviolet.svg" alt="SDK">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

---

## ✨ Features

- **🔒 Secure & Random PIN Generation**: Generates high-entropy PINs using robust pseudo-random algorithms (`SecurePinGenerator`).
- **⚙️ Advanced Customization Rules**:
  - Customizable PIN length (from **4 to 12** digits).
  - **Prevent Repeated Digits**: Ensure all digits in the PIN are unique (0-9 used at most once).
  - **Prevent Consecutive Digits**: Ensure no two adjacent digits are consecutive (e.g., `1-2`, `2-1`) or identical (`1-1`).
  - **Custom Prefix & Suffix**: Optionally prepend or append fixed numeric prefixes and suffixes.
- **🎨 Modern Material 3 UI**: Clean, intuitive interface adhering to Material Design 3 guidelines with smooth animations and dynamic color support.
- **🌗 Theme Support**: Full Light and Dark mode support, easily toggleable or synced with system settings.
- **📳 Haptic Feedback**: Tactile vibrations on generation, copying, and interaction.
- **💾 Persistent Preferences**: User configurations and history are securely saved using **Jetpack DataStore**.
- **📋 One-Tap Actions**: Instantly copy generated PINs to clipboard with visual confirmation.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) with unidirectional data flow (UDF) using `StateFlow`.
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation).
- **Data Persistence**: [Jetpack DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore).
- **Testing**: JUnit 4, AndroidX Instrumentation Tests, and Compose UI Testing.

---

## 📂 Project Structure

```text
com.example.pinmaker
│
├── data/                  # Repository & DataStore preference management
├── generator/             # Core PIN generation logic & secure algorithms
├── model/                 # Data models, UI states, and configuration rules
├── ui/
│   ├── components/        # Reusable UI elements (Haptic helpers, etc.)
│   ├── navigation/        # Nav graph & screen definitions
│   ├── screens/           # Feature screens (Home, Settings, About, Splash)
│   ├── theme/             # Material 3 colors, typography, and shapes
│   └── viewmodel/         # ViewModels managing UI state and business logic
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Koala or newer recommended)
- **JDK 11** or higher
- Android SDK with **Compile SDK 37** and **Min SDK 26**

### Cloning & Building
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/PinMaker.git
   cd PinMaker
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run the app on an emulator or physical Android device.

---

## 🧪 Testing

Run unit tests locally via Gradle:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
