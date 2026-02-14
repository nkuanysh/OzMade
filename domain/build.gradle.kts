plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("kotlin-kapt")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}
