# Nightell → Kotlin Multiplatform: Migration Plan

## Goal

Share the data, domain and presentation logic (networking, storage, repositories,
use cases, ViewModels) between Android and iOS, so that each platform only adds
its own UI and media playback.

**Guiding rule:** the Android app stays shippable after every phase. Each phase is
its own PR, and you can release it to Play before starting the next one.

> **Update:** the clean-architecture refactor on branch `refactor/clean-architecture` already covers part of this plan:
> - `domain` is now pure Kotlin: no DTOs, Room or Android.
> - Repositories return domain models, and DTO → model mappers live in `data/mapper`.
> - RxJava is removed.
> - ViewModels use `StateFlow`.
> - Navigation passes ids instead of JSON.
>
> This makes Phases 1 and 4 mostly a matter of moving files into `:shared`. Some rows in the table below describe the code before that refactor.

## Current state (findings that shape the plan)

| Area | Finding | Impact |
|---|---|---|
| Toolchain | Kotlin 2.0.0, AGP 8.5.1, Gradle 8.7, `kapt` for Hilt and Room. A KSP plugin is declared at `1.9.21-1.0.15` (mismatched) but not used | Bump to Kotlin 2.1.x and move to KSP, which Room KMP requires |
| Models / DTOs | Plain data classes with snake_case properties that match the JSON. No `@SerializedName`, no `@Parcelize` | Switching to kotlinx.serialization only needs `@Serializable` on each class |
| Navigation args | `User` is passed as a Gson JSON string in routes (`NavigatinController.kt`, 3 places, plus `utils/Utility.kt`) | Change to kotlinx.serialization JSON, or pass `userId` and load the user |
| Network | Retrofit `ApiService` (~35 endpoints). Auth, retry, rate-limit and offline interceptors | Rewrite as a Ktor client with plugins |
| Rate limiter | RxJava3 (`RateLimiter.kt`), the only RxJava use | Replace with a coroutine `Mutex` and a token bucket; drop RxJava |
| Repositories | Same pattern repeated in every method: `try { handleApiResponse(api.x()) } catch …` | Move to shared code mostly unchanged; `handleApiResponse` gets a Ktor version |
| Room | 1 entity (`PostEntity`), 1 DAO, schema version 1 | Upgrade to Room 2.7+ KMP. Keep the database name and schema so bookmarks survive the update |
| Prefs | `TokenManager` uses SharedPreferences | Move to multiplatform-settings, keeping the same prefs file name so users stay logged in |
| Android-only in data layer | `ConnectivityObserver`, `FileUtils`, `FileHandler` (content URIs), Retry/RateLimiter interceptors | Shared interfaces with Android and iOS implementations (`expect`/`actual`) |
| DI | Hilt: 5 modules, 12 `@HiltViewModel` | Koin (supports KMP) |
| ViewModels | LiveData in 9 ViewModels and 2 screens | Change to `StateFlow`, using androidx `lifecycle-viewmodel` (KMP) |
| Media | Media3 ExoPlayer, MediaSession service, notification (Glide) | Stays on Android; add a shared `AudioPlayer` interface |
| Tests | 16 unit test classes using Mockito, MockK and PowerMock (JVM-only) | Shared tests use fakes; Android-only tests stay |
| CI | Workflow triggers on `main` (the repo uses `master`) and sets up JDK 11 (the project needs 17) | Fix before starting |

## Target module layout

```
nightell/
├── shared/                      # KMP library module
│   └── src/
│       ├── commonMain/          # models, DTOs, Ktor API, repositories, use cases,
│       │                        # ViewModels, Room DB, settings, Koin modules
│       ├── androidMain/         # actuals: connectivity, file reading, DB builder, HTTP engine (OkHttp)
│       ├── iosMain/             # actuals: connectivity, file reading, DB builder, HTTP engine (Darwin)
│       └── commonTest/
├── app/                         # Android app: Compose UI, MediaService, notifications
└── iosApp/                      # Xcode project (added in Phase 6)
```

Package names stay `com.neatplex.nightell.*`, so moving files doesn't force changes to their imports.

---

## Phase 0: Prep (no behaviour change)

1. Fix CI: trigger on `master`, use JDK 17, run `./gradlew build test`.
2. Upgrade the toolchain: Kotlin 2.1.x, AGP 8.7+, Gradle 8.10+, KSP that matches the Kotlin version.
3. Move Hilt and Room from `kapt` to KSP.
4. Remove unused dependencies: `androidx.wear.compose`, `legacy-support-v4`, and the mismatched test versions (`kotlin-test-junit:1.5.30`, `hilt-android-testing:2.37`).
5. Add a version catalog (`gradle/libs.versions.toml`). Two modules will share versions from now on.

**Done when:** CI is green on `master` and the app behaves exactly as before.

## Phase 1: Shared module + models

1. Create `:shared` with the `kotlin("multiplatform")` and `com.android.library` plugins. Targets: `androidTarget()`, `iosArm64()`, `iosSimulatorArm64()`.
2. Make `:app` depend on `:shared`.
3. Move `domain/model/*` (except `PostEntity`) and `data/dto/*` to `commonMain`, and add `@Serializable` to them.
4. Move `utils/Result.kt` (the `Result` type only), `Validation`/`IValidation` and `getTimeAgo` to shared code. Replace any `java.*` time APIs with kotlinx-datetime.
5. Replace Gson in `Utility.kt` and the navigation routes with `kotlinx.serialization.json`.
6. Retrofit keeps working in this phase. Add `kotlinx-serialization-converter` so Retrofit reads the same `@Serializable` classes, then remove Gson.

**Done when:** the models compile for iOS (`./gradlew :shared:compileKotlinIosArm64`) and the Android app runs.

## Phase 2: Networking with Ktor

1. Write `NightellApi` in `commonMain`: one Ktor `HttpClient`, one function per endpoint, with the same names as `ApiService`.
2. Use plugins:
   - `ContentNegotiation` with kotlinx.serialization.
   - `Auth` (bearer), or a small custom plugin that reads the token from settings. On 401, call `logoutForce()`, as `AuthInterceptor` does today.
   - `HttpRequestRetry` with 3 retries, replacing `RetryInterceptor`.
   - A custom `RateLimit` plugin built on coroutines, replacing `RateLimiter` and RxJava. **Revisit the limit:** 8 requests per 2 minutes is very strict.
   - An offline check through a shared `ConnectivityMonitor` interface.
   - `HttpCache`, replacing the OkHttp disk cache.
3. Engines: OkHttp on Android, Darwin on iOS.
4. Multipart file upload goes through `submitFormWithBinaryData`. Reading the file bytes is platform-specific (`FileReader` expect/actual), which replaces the `FileUtils`/`FileHandler` URI logic.
5. Rewrite `handleApiResponse` for Ktor's `HttpResponse`, keeping the same error-message mapping.
6. Switch repositories from `ApiService` to `NightellApi` one feature at a time: Like → Follow → Comment → Post → User → File → Auth. Then delete Retrofit.

**Done when:** Retrofit, OkHttp interceptors and RxJava are gone, and all repository tests pass against Ktor `MockEngine`.

## Phase 3: Storage

1. Upgrade Room to 2.7+ KMP. Move `AppDatabase`, `SavedPostDao` and `PostEntity` to `commonMain`, and use `BundledSQLiteDriver`.
2. Use a platform `getDatabaseBuilder()` (expect/actual). On Android, **keep the same DB file name** so existing bookmarks survive.
3. Replace `TokenManager` with `multiplatform-settings`. On Android, back it with the existing `"token_preference"` SharedPreferences file so users stay logged in.
4. Write a migration test that installs the old version, adds a bookmark and logs in, upgrades, and checks that both are still there.

**Done when:** an update from the current Play build keeps the login and bookmarks.

## Phase 4: DI + repositories + use cases + ViewModels

1. Add Koin to `commonMain`: `networkModule`, `storageModule`, `repositoryModule`, `useCaseModule`, `viewModelModule`, plus an `expect val platformModule` for each platform.
2. Move the repository interfaces and implementations, and all use cases, to shared code. Remove `@Inject`.
3. For each ViewModel (12 in total), change `LiveData` to `StateFlow` and move it to `commonMain` (androidx `lifecycle-viewmodel` 2.8+ supports KMP). Screens collect state with `collectAsStateWithLifecycle()`.
   Order: Search → Like/Follow parts → User → Profile → Post → Home → Upload → Auth, with Shared/Connectivity/Logout/Database last.
4. Remove Hilt from `:app`. `MyApplication` starts Koin, and screens use `koinViewModel()`.
5. Move the tests: anything that tests shared code goes to `commonTest` and uses fakes instead of Mockito/MockK.

**Done when:** `:app` contains only UI, the media service and Android glue, and shared tests run on JVM and iOS simulator.

## Phase 5: Media abstraction

1. Define `AudioPlayer` and `PlaybackState` in `commonMain`. Model them on the current `MediaServiceHandler`'s `PlayerEvent` and `SimpleMediaState`.
2. On Android, the implementation wraps the existing ExoPlayer/MediaSession service. While doing this, fix the progress loop that runs forever on `GlobalScope`.
3. On iOS, implement it with `AVPlayer`, `MPNowPlayingInfoCenter` and `MPRemoteCommandCenter`. Enable the background audio mode.
4. Move `MediaViewModel` to shared code.

## Phase 6: iOS app

Decide on the UI approach before starting this phase:

- **Option A: SwiftUI (recommended to start).** Native look; uses shared ViewModels through SKIE or KMP-NativeCoroutines to work with flows. The Android Compose UI doesn't change.
- **Option B: Compose Multiplatform.** Shares the UI too. It needs Glide replaced with Coil 3, Accompanist swipe-refresh with Material3 `PullToRefreshBox`, the navigation-compose (KMP) version, and Compose resources instead of `R.drawable`. More reuse, but a bigger change.

In either case: Google Sign-In uses the GoogleSignIn iOS SDK behind a shared `GoogleAuthProvider` interface, file and image picking goes through PHPicker and the document picker, and notification permission needs handling.

---

## Risks & mitigations

| Risk | Mitigation |
|---|---|
| Users lose login or bookmarks on update | Keep the prefs and DB file names, and run the Phase 3 migration test before release |
| Ktor behaves differently from OkHttp (timeouts, caching, multipart) | Run the Ktor `MockEngine` tests, and do a staged Play rollout after Phase 2 |
| A Koin misconfiguration only shows up at runtime | Add a `checkModules()`/`verify()` unit test in CI |
| The big-bang ViewModel migration stalls | Migrate one ViewModel per PR; Hilt and Koin can run side by side for a while |
| iOS builds need a Mac | Use a macOS runner in CI for `:shared` iOS tests from Phase 1 |

## Rough effort (one developer)

| Phase | Estimate |
|---|---|
| 0 Prep | 1–2 days |
| 1 Shared module + models | 2–3 days |
| 2 Ktor | 4–6 days |
| 3 Storage | 2–3 days |
| 4 DI / repos / ViewModels | 5–8 days |
| 5 Media abstraction | 3–5 days |
| 6 iOS app (SwiftUI) | 3–5 weeks |

Phases 0–5 (shared code, Android only): about **4–5 weeks**. Phase 6 depends on the UI choice.

## Decisions needed before starting

1. UI on iOS: SwiftUI (A) or Compose Multiplatform (B)?
2. DI: Koin (recommended) or keep Hilt on Android with manual wiring in shared code?
3. Room KMP (keeps existing code) or SQLDelight?
4. Is the 8 requests / 2 min client rate limit intentional?
