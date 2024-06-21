plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.neatplex.nightell"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.neatplex.nightell"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        exclude ("META-INF/gradle/incremental.annotation.processors")
    }

    buildFeatures{
        dataBinding = true
        viewBinding = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.wear.compose:compose-material:1.3.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.foundation:foundation:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.navigation:navigation-compose:2.7.6")


    // Retrofit and Coroutines
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Google auth
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")


    // Shared Preferences
    implementation ("androidx.preference:preference-ktx:1.2.1")

    // Coil loading images
    implementation ("io.coil-kt:coil-compose:2.2.2")


    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-work:1.1.0")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.0")

    implementation ("androidx.compose.material:material:1.6.1")

    // Exo Player
    implementation ("androidx.media3:media3-exoplayer:1.3.1")
    implementation ("androidx.media3:media3-ui:1.3.1")
    implementation ("androidx.media3:media3-common:1.3.1")
    implementation ("androidx.media3:media3-session:1.3.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //Pull To Refresh
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.35.1-alpha")

    // Local Unit Tests
    testImplementation ("org.jetbrains.kotlin:kotlin-test-junit:1.5.30")
    testImplementation ("org.mockito:mockito-core:3.11.2")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation ("org.mockito:mockito-inline:3.11.2")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")

    testImplementation ("junit:junit:4.13.2")
    testImplementation ("androidx.test:core:1.5.0")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")
    testImplementation ("com.google.truth:truth:1.1.3")
    testImplementation ("com.squareup.okhttp3:mockwebserver:4.9.1")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:1.6.7")
    testImplementation ("io.mockk:mockk:1.10.5")

    // Instrumentation Tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.37")
    androidTestImplementation("com.google.dagger:hilt-android-compiler:2.37")
}
