plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.example.beattreat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.beattreat"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.example.beattreat.HiltTestRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        excludes += "/META-INF/LICENSE.md"
        excludes += "/META-INF/LICENSE-notice.md"
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")


    // Navegación e íconos
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.compose.material:material-icons-extended")

    // Hilt
    implementation(libs.dagger.hilt)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.material3)
    //ksp(libs.dagger.kapt)
    ksp(libs.hilt.compiler)

    implementation(libs.hilt.compose.navigation)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)


    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Google Maps Compose
    // implementation("com.google.maps.android:maps-compose:4.3.3")
    // Google Play Services Maps (SDK nativo requerido por maps-compose)
    // implementation("com.google.android.gms:play-services-maps:18.2.0")
    // FusedLocationProviderClient (obtiene coordenadas GPS)
    // implementation("com.google.android.gms:play-services-location:21.2.0")

    // OSMDroid - OpenStreetMap para Android
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    // FusedLocationProviderClient (coordenadas GPS)
    implementation("com.google.android.gms:play-services-location:21.2.0")


    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Adicionales
    testImplementation("io.mockk:mockk:1.13.14")

//Coroutines test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

//Alternativa a los asserts tradicionales
    testImplementation("com.google.truth:truth:1.4.2")

    androidTestImplementation("com.google.truth:truth:1.1.5")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.52")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
    testImplementation(kotlin("test"))

    // e2e
// AndroidX Test - Instrumented
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

// ActivityScenario
    androidTestImplementation("androidx.test:core:1.5.0")

// Para UI / instrumented tests (androidTest)
    androidTestImplementation("io.mockk:mockk-android:1.13.14")

    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

}