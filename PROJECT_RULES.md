# Project Rules: CaliTech (Kotlin Multiplatform)

Always follow these guidelines and rules when writing code or making architectural decisions in this project:

## Core Principles
1. **Latest Technologies:** Use only the latest technologies, recommended approaches, and best practices.
2. **KMP Focus:** This is a Kotlin Multiplatform (KMP) project targeting **Android, iOS, and Desktop**.
3. **Architecture (MVVM):** Strictly adhere to the **MVVM (Model-View-ViewModel)** architecture across the application. Use recommended state management with Coroutines/StateFlow, and modern UI patterns.
4. **SOLID Principles:** Always follow **SOLID principles** to ensure the codebase remains maintainable, scalable, and robust.
5. **Design Patterns:** Utilize established design patterns (e.g., Gang of Four patterns like Factory, Builder, Observer, Strategy) wherever they provide a clean, structural advantage to the code.
6. **KMP Design:** Follow the latest KMP design patterns and utilize recommended KMP libraries for shared logic.

## Preferred Libraries
- **Networking:** Use **Ktor**.
- **Dependency Injection:** Use **Koin**.
- **UI Framework:** Use **Compose Multiplatform** (for shared UI across platforms where applicable, and native Compose on Android).

## Platform-Specific Rules
### iOS
- **Dependency Management:** If iOS native dependencies are needed, use **Swift Package Manager (SPM)**. 
- **CRITICAL:** Do NOT use CocoaPods.

### Android & Desktop
- Adopt modern native toolchains and maintain clean, reactive architectures (e.g., modern Android Jetpack libraries that are KMP-compatible where possible).
