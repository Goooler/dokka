plugins {
    id("com.android.library")
    id("org.jetbrains.dokka")
    kotlin("android")
}

apply(from = "../template.root.gradle.kts")

android {
    compileSdk = 29
    namespace = "org.jetbrains.dokka.it.android"
    defaultConfig {
        minSdk = 21
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("androidx.appcompat:appcompat:1.1.0")
}

