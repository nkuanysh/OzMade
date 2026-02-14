plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":domain"))
    api(platform(libs.firebase.bom))
    implementation(libs.hilt.android)
    implementation(libs.firebase.auth.ktx)
    kapt(libs.hilt.compiler)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.javax.inject)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
}
