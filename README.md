<div align="center">

# 🏦 Banker's Algorithm Simulator

### *An interactive deadlock-avoidance visualizer & simulator for Android*

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-brightgreen?style=for-the-badge)](https://developer.android.com/about/dashboards)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-orange?style=for-the-badge)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

**Banker's Algorithm Simulator** is a modern Android application engineered with **Jetpack Compose** and **Material Design 3** to visualize and demonstrate **Edsger Dijkstra's Banker's Algorithm** for operating systems deadlock avoidance. It provides a hands-on pedagogical tool for computer science students, educators, and systems engineers to explore safety algorithms, resource request evaluation, and process allocation dynamics in real time.

</div>

---

## 📖 Table of Contents

- [✨ Key Features](#-key-features)
- [🧠 How Banker's Algorithm Works](#-how-bankers-algorithm-works)
  - [1. Data Structures](#1-data-structures)
  - [2. Safety Algorithm](#2-safety-algorithm)
  - [3. Resource-Request Algorithm](#3-resource-request-algorithm)
- [📱 App Screens & Workflow](#-app-screens--workflow)
- [🛠️ Architecture & Tech Stack](#️-architecture--tech-stack)
- [📂 Project Structure](#-project-structure)
- [🚀 Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Clone & Build](#clone--build)
  - [Running Unit Tests](#running-unit-tests)
- [🧪 Test Coverage](#-test-coverage)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [👥 Authors](#-authors)

---

## ✨ Key Features

- **⚡ Flexible System Configuration**:
  - Configure arbitrary numbers of processes ($n$) and resource types ($m$).
  - Enter custom Available vectors, Max claim matrices, and Current Allocation matrices.
  - One-tap **"Randomize"** button to automatically generate valid test scenarios.
  - One-tap **"Clear All"** to reset and re-enter inputs effortlessly.

- **🛡️ Real-time Safety State Determination**:
  - Automatically calculates the **Need Matrix**: $\text{Need}[i][j] = \text{Max}[i][j] - \text{Allocation}[i][j]$.
  - Runs Dijkstra's Safety Algorithm to verify if the system is in a **SAFE** or **UNSAFE** state.
  - Dynamically renders the computed safe sequence (e.g., $P_1 \to P_3 \to P_4 \to P_0 \to P_2$) using clean visual chips and direction indicators.

- **🔍 Expandable Step-by-Step Algorithm Trace**:
  - View every single evaluation step taken by the algorithm.
  - Inspect current `Work` vector, process `Need`, whether the process can proceed ($\text{Need}_i \le \text{Work}$), and the resulting new `Work` vector after reclaiming resources.

- **🔄 Dynamic Resource Request Simulation**:
  - Test real-time resource requests for any specific process ($P_i$).
  - Evaluates Rule 1: $\text{Request} \le \text{Need}_i$ (cannot exceed declared maximum need).
  - Evaluates Rule 2: $\text{Request} \le \text{Available}$ (cannot exceed available system resources).
  - Performs hypothetical allocation and safety check before granting.
  - Automatically rolls back state when requests violate safety rules and presents clear human-readable explanations.

- **📚 Interactive Theory & Educational Guide**:
  - Integrated educational view explaining core concepts: Safe State, Unsafe State, Deadlock, and the role of the Banker's Algorithm in modern OS design.

- **🎨 Modern Material 3 UI**:
  - Built 100% in Jetpack Compose with adaptive layouts, smooth expandable animations, dynamic color theming, and input sanitization.

---

## 🧠 How Banker's Algorithm Works

The Banker's Algorithm is a classic deadlock-avoidance algorithm developed by Edsger Dijkstra. It tests for safety by simulating the allocation for predetermined maximum possible amounts of all resources, and then makes an "s-state" check to test for possible activities, before deciding whether allocation should be allowed to continue.

### 1. Data Structures

Let $n$ be the number of processes and $m$ be the number of resource types:

- **Available**: Vector of length $m$. If $\text{Available}[j] = k$, there are $k$ instances of resource type $R_j$ available.
- **Max**: $n \times m$ matrix. If $\text{Max}[i][j] = k$, process $P_i$ may request at most $k$ instances of resource type $R_j$.
- **Allocation**: $n \times m$ matrix. If $\text{Allocation}[i][j] = k$, process $P_i$ is currently allocated $k$ instances of resource type $R_j$.
- **Need**: $n \times m$ matrix. Remaining resources needed by process $P_i$:
  $$\text{Need}[i][j] = \text{Max}[i][j] - \text{Allocation}[i][j]$$

### 2. Safety Algorithm

1. Let $\text{Work} = \text{Available}$ and $\text{Finish}[i] = \text{false}$ for $i = 0, 1, \dots, n-1$.
2. Find an index $i$ such that:
   $$\text{Finish}[i] == \text{false} \quad \text{and} \quad \text{Need}_i \le \text{Work}$$
   If no such $i$ exists, go to step 4.
3. If such an $i$ exists:
   $$\text{Work} = \text{Work} + \text{Allocation}_i$$
   $$\text{Finish}[i] = \text{true}$$
   Append $P_i$ to the safe sequence and repeat step 2.
4. If $\text{Finish}[i] == \text{true}$ for all $i$, then the system is in a **Safe State**. Otherwise, it is **Unsafe**.

### 3. Resource-Request Algorithm

When process $P_i$ requests resources $\text{Request}_i$:
1. If $\text{Request}_i \le \text{Need}_i$, proceed to step 2. Otherwise, raise an error condition since the process has exceeded its maximum claim.
2. If $\text{Request}_i \le \text{Available}$, proceed to step 3. Otherwise, $P_i$ must wait since resources are not currently available.
3. Pretend to allocate resources to $P_i$ by modifying the state:
   $$\text{Available} = \text{Available} - \text{Request}_i$$
   $$\text{Allocation}_i = \text{Allocation}_i + \text{Request}_i$$
   $$\text{Need}_i = \text{Need}_i - \text{Request}_i$$
4. Run the Safety Algorithm on this speculative state:
   - If safe $\to$ **Grant resources** and commit state.
   - If unsafe $\to$ **Deny request**, roll back state, and make $P_i$ wait.

---

## 📱 App Screens & Workflow

```
       ┌────────────────────────┐
       │      Home Screen       │
       └───────────┬────────────┘
                   │
         ┌─────────┴─────────┐
         ▼                   ▼
┌──────────────────┐  ┌──────────────────┐
│  Learn / Theory  │  │   Input Screen   │
│   (Concepts)     │  │ (n, m, Matrices) │
└──────────────────┘  └────────┬─────────┘
                               │ "Run Simulation"
                               ▼
                      ┌──────────────────┐
                      │  Result Screen   │
                      │  - Safe Sequence │
                      │  - Trace Steps   │
                      │  - Need Matrix   │
                      └────────┬─────────┘
                               │ "Submit Request"
                               ▼
                      ┌──────────────────┐
                      │  Request Screen  │
                      │ (Grant / Deny +  │
                      │   Rollback)      │
                      └──────────────────┘
```

1. **Home Screen**: Welcoming dashboard with direct access to start simulations or learn theoretical foundations.
2. **Introduction Screen**: Digestible reference cards on Deadlock, Safe States, Unsafe States, and Matrix mathematics.
3. **Input Screen**: Responsive matrix editor with step-by-step guidance, real-time validation, and random scenario generator.
4. **Result Screen**: Status banner (Safe/Unsafe), animated sequence chips, expandable algorithmic trace log, and read-only Need & Allocation tables.
5. **Request Screen**: Process selector, vector inputs, instant evaluation, and safety validation feedback.

---

## 🛠️ Architecture & Tech Stack

This project follows Google's recommended Android architecture practices:

- **Language**: [Kotlin](https://kotlinlang.org/) (Coroutines, StateFlow, Functional transformations)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
- **Navigation**: [Navigation Compose](https://developer.android.com/guide/navigation)
- **Testing**: [JUnit 4](https://junit.org/junit4/) for algorithm correctness and state rollback verification
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`) with version catalogs (`libs.versions.toml`)

---

## 📂 Project Structure

```
BankersAlgorithmSimulator/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mahin/bankersalgorithmsimulator/
│   │   │   │   ├── algorithm/              # Core algorithmic logic
│   │   │   │   │   ├── BankersAlgorithm.kt # Safety & Request algorithm implementations
│   │   │   │   │   └── InputValidator.kt   # Strict input validation logic
│   │   │   │   ├── model/                  # Immutable data models
│   │   │   │   │   ├── InputValidation.kt  # Validation error types & payloads
│   │   │   │   │   ├── Process.kt          # Process representation (Allocation, Max, Need)
│   │   │   │   │   └── Simulation.kt       # Simulation state, trace steps, and results
│   │   │   │   ├── navigation/             # Navigation graph & route definitions
│   │   │   │   │   ├── NavGraph.kt
│   │   │   │   │   └── Screen.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/         # Reusable UI components
│   │   │   │   │   │   ├── MatrixTable.kt
│   │   │   │   │   │   ├── SafeSequenceRow.kt
│   │   │   │   │   │   └── VectorInputRow.kt
│   │   │   │   │   ├── screens/            # Full-page Compose screens
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── InputScreen.kt
│   │   │   │   │   │   ├── IntroductionScreen.kt
│   │   │   │   │   │   ├── RequestScreen.kt
│   │   │   │   │   │   └── ResultScreen.kt
│   │   │   │   │   └── theme/              # Color schemes, Typography, Material Theme
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   ├── viewmodel/              # State holders & business logic
│   │   │   │   │   └── SimulationViewModel.kt
│   │   │   │   └── MainActivity.kt         # Single-activity entry point
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── java/com/mahin/bankersalgorithmsimulator/
│   │           └── algorithm/
│   │               └── BankersAlgorithmTest.kt # Comprehensive unit tests
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug / Meerkat (or newer)
- **JDK**: Java 11 or higher
- **Android Device / Emulator**: Running Android 8.0 (API 26) or higher

### Clone & Build

```bash
# Clone the repository
git clone https://github.com/wahiPP/BankersAlgorithmSimulator.git

# Navigate to the project directory
cd BankersAlgorithmSimulator

# Build the project via Gradle wrapper
./gradlew assembleDebug
```

You can now open the project in Android Studio and run it on an emulator or physical device.

### Running Unit Tests

The core algorithm and edge cases (safe state sequence computation, starvation/unsafe conditions, valid request granting, and rollback on denial) are covered by automated unit tests:

```bash
./gradlew test
```

---

## 🧪 Test Coverage

The test suite in [`BankersAlgorithmTest.kt`](app/src/test/java/com/mahin/bankersalgorithmsimulator/algorithm/BankersAlgorithmTest.kt) validates:
- ✅ Classic 5-process, 3-resource textbook example returns safe sequence `[P1, P3, P4, P0, P2]`.
- ✅ Complete resource starvation scenarios correctly return `SafetyResult.Unsafe`.
- ✅ Dynamic valid resource request is successfully granted and state is updated.
- ✅ Request exceeding declared Need is denied and triggers rollback.
- ✅ Request exceeding current Available resources is denied without modifying existing state.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to check out the [issues page](https://github.com/wahiPP/BankersAlgorithmSimulator/issues).

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Authors

- **Wahiduzzaman Mahin**
  - GitHub: [@wahiPP](https://github.com/wahiPP)
  - Email: [wahiduzzamanmahn@gmail.com](mailto:wahiduzzamanmahn@gmail.com)

- **Konokuzzaman**
  - GitHub: [@konokuzzaman](https://github.com/konokuzzaman)
  - Email: [konokuzzaman12928@gmail.com](mailto:konokuzzaman12928@gmail.com)

