# Abrakadabra – Android Frontend App

This is the **Android frontend** of the Abrakadabra App. It is a native Android project built with Java/Kotlin using Gradle.

---

## 📁 Project Structure

```
.
├── app/
│   ├── release/                  # Release-related configs
│   ├── src/                      # Source code of the app
│   ├── build.gradle              # App-level Gradle config
│   └── proguard-rules.pro        # ProGuard config for minification
├── gradle/                       # Gradle wrapper files
├── images/                       # Image resources
├── ucrop/                        # uCrop image cropping module
├── .gitignore
├── README.md
├── build.gradle                  # Project-level Gradle config
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle
```

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/abrakadabra-android.git
cd abrakadabra-android
```

### 2. Open in Android Studio

- Open Android Studio
- Click **Open an existing project**
- Select the root directory of the cloned repo

---

## Build & Run

To build and run the app:

- Click **Run** in Android Studio
- Or use terminal:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

---

## Release Build

To generate a signed APK:

1. Set up signing configs in `app/build.gradle`
2. Run:

```bash
./gradlew assembleRelease
```
