# Suzuki Connect (static UI demo)
Kotlin · Jetpack Compose · Navigation Compose · MVVM · Clean Architecture
Package: com.example.suzukiconnect

Open the folder in Android Studio (Koala or newer, JDK 17), let Gradle sync, run on a device/emulator (minSdk 24).

Layers
- domain/        models, repository interface, use cases (pure Kotlin)
- data/          static data source + repository implementation
- di/            AppContainer (manual DI)
- presentation/  theme, components, navigation (NavHost), home, health (Screen + ViewModel)
