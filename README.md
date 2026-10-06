# Calculator & Currency Converter 🧮 💱

A modern, high-performance Android calculator and live currency converter built with **Jetpack Compose** and **Material 3**. Engineered for Android SDK 37 with a fluid **Liquid Glassmorphic** UI, scientific calculations, calculation history, and real-time online exchange rates.

---

## ✨ Features

- **🧮 Advanced Calculator**:
  - Full arithmetic and percentage operations.
  - **Scientific Mode (Fx)**: `sin`, `cos`, `tan`, `log`, `ln`, `√`, `π`, `e`, `^`, `±`.
  - Non-overlapping, responsive capsule keypad design.
  - Haptic touch feedback on button interactions.
  - Long-press display to copy calculation results directly to clipboard.

- **💱 Real-Time Currency Converter**:
  - Live exchange rates fetched automatically from online financial API.
  - Support for **30+ global and Arab currencies** (USD, EUR, GBP, SAR, EGP, AED, KWD, QAR, JPY, TRY, and more).
  - Instant live conversion as you type.
  - Quick amount presets (`1`, `10`, `50`, `100`, `500`, `1000`).
  - Searchable currency selector bottom sheet.
  - Offline mode with cached fallback rates.
  - One-tap currency swap (`⇅`).

- **📜 Calculation History**:
  - Persistent tape recording expressions and results.
  - Tap any past calculation to restore and reuse its result.
  - One-tap history clear.

- **💧 Liquid Glass Floating Dock**:
  - Ultra-minimalist, translucent fluid glass navigation dock.
  - Icon-only interface with liquid droplet active highlight.
  - Elevated floating design ensuring zero obstruction of keypad controls.

- **🌓 Automatic Theme Detection**:
  - Seamlessly adapts between Day (Light) and Night (Dark) modes based on system settings.
  - High-contrast, OLED-friendly surfaces in Dark Mode and crisp aesthetics in Light Mode.

- **📐 Strict LTR Layout**:
  - Maintains universal mathematical keypad order (Left-to-Right) across all system languages.

---

## 🛠️ Tech Stack & Specifications

| Component | Specification |
| :--- | :--- |
| **Language** | Kotlin 2.4.20 |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Compile SDK** | 37 |
| **Target SDK** | 37 |
| **Min SDK** | 26 (Android 8.0 Oreo) |
| **Gradle** | 9.6.0 Wrapper |
| **AGP** | 9.4.1 (Built-in Kotlin) |
| **Architecture** | MVVM with Kotlin Coroutines & StateFlow |

---

## 🚀 Building & Installing

### Prerequisites
- Android SDK 37 (Platform tools & Build tools 37.0.0)
- Java 17 or Java 21 JDK

### Build Debug APK
```bash
./gradlew assembleDebug
```
The compiled APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License
This project is open source and available under the [MIT License](LICENSE).
