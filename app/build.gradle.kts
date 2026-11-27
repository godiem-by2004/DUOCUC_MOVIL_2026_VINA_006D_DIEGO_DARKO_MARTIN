plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.example.agrogestion"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.agrogestion"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("agrogestion-release.jks")
            storePassword = "agro2026"
            keyAlias = "agrogestion_key"
            keyPassword = "agro2026"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime")
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.material:material-icons-extended")

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // 1. Navegación Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // 2. Room (Base de Datos Local SQLite para Modo Offline)
    val roomVersion = "2.7.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    add("kapt", "androidx.room:room-compiler:$roomVersion")

    // 3. DataStore (Preferencias locales del asesor)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // 4. Carga de imágenes (Coil): muestra las fotos foliares guardadas en disco.
    //    La cámara se usa con ActivityResultContracts.TakePicture + FileProvider (no requiere CameraX).
    implementation("io.coil-kt:coil-compose:2.7.0")

    // 5. Retrofit + Gson + Logging (Microservicios Spring Boot y API Externa)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 6. Pruebas Unitarias (IE 3.2.1 - Cobertura >= 80%)
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
