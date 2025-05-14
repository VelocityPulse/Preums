# Preums Project

## 🚀 Project Overview
This is an Android application developed using Kotlin and Jetpack Compose.

## 📐 Coding Preferences and Best Practices

### 🏗️ Project Structure
- Organized modular structure with clear separation of concerns
- Packages grouped by feature and functionality:
  - `core`: Core application components
  - `play`: Main application logic
  - `exercise`: UI and experimental components
  - `network`: Networking-related classes

### 🧩 Architecture Patterns
- MVVM (Model-View-ViewModel) architecture
- Dependency Injection (DI) using modules
- Reactive UI with Jetpack Compose

### 💻 Kotlin Coding Conventions
- Prefer immutable data (`val`) over mutable (`var`)
- Use data classes for model representations
- Leverage Kotlin's null safety features
- Utilize extension functions and functional programming concepts

### 🎨 UI Development
- Jetpack Compose for modern, declarative UI
- Consistent theming (color, typography)
- Modular UI components
- Responsive design considerations

### 🔒 Best Practices
- Separate network, UI, and business logic
- Use view models for state management
- Implement error handling and network failure dialogs
- Create reusable UI components

### 🧪 Testing
- Unit tests for core logic
- Instrumented tests for Android-specific components

### 🛠️ Build and Configuration
- Gradle Kotlin DSL (`build.gradle.kts`)
- ProGuard rules for code optimization
- Resource management through XML and Kotlin

## 📦 Dependencies
- Kotlin
- Jetpack Compose
- Android SDK
- Networking libraries (to be specified)

## 🤝 Contributing
1. Follow existing code structure
2. Maintain consistent code style
3. Write tests for new features
4. Document significant changes

## 📝 Notes
- Project is a work in progress
- Experimental features in `exercise` package
