# Preums Project

## 🚀 Project Overview
A local network discovery and interaction Android application developed using Kotlin and Jetpack Compose, focusing on dynamic network connectivity and state management.

## 🌐 Key Features
- Dynamic WiFi network state tracking
- Local network host/client discovery
- Flexible state machine for application flow
- Reactive network interaction

## 📐 Project Structure
### 🏗️ Architectural Components
- `core`: Core application infrastructure
- `play`: Main application logic and state management
- `network`: Network discovery and communication classes

## 🧩 Technical Architecture
- MVVM with Finite State Machine (FSM)
- Dependency Injection via Koin
- Reactive programming with Kotlin Coroutines
- Jetpack Compose for UI

## 🔍 Current Development Stage
- Network connectivity state management implemented
- Basic host discovery infrastructure
- Flexible state transitions defined

### 📡 Network States
- Standing for WiFi
- Menu Selection
- Host Discovery
- Server Research
- Network Failure
- Active Play Mode

## 💻 Technical Stack
- Language: Kotlin
- UI Framework: Jetpack Compose
- Dependency Injection: Koin
- Asynchronous Programming: Kotlin Coroutines
- Minimum SDK: 29
- Target SDK: 35

## 🚧 Next Development Steps
- Complete network host connection logic
- Implement user interaction flows
- Develop comprehensive UI components
- Add error handling and network event management

## 📦 Core Dependencies
- Kotlin
- Android Jetpack
- Koin
- Coroutines

- Networking libraries (to be specified)

## 🤝 Contributing
1. Follow existing code structure
2. Maintain consistent code style
3. Write tests for new features
4. Document significant changes

## 📝 Notes

## 🚨 Pending TODOs
- Complete server response handling in `HostClient`
- Implement `stopProcedures()` method in `HostClient`
- Project is a work in progress
- Experimental features in `exercise` package
