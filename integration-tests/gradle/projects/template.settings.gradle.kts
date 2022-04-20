@file:Suppress("LocalVariableName", "UnstableApiUsage")

pluginManagement {
    val dokka_it_kotlin_version: String by settings
    val dokka_it_android_gradle_plugin_version: String? by settings

    plugins {
        id("org.jetbrains.kotlin.js") version dokka_it_kotlin_version
        id("org.jetbrains.kotlin.jvm") version dokka_it_kotlin_version
        id("org.jetbrains.kotlin.android") version dokka_it_kotlin_version
        id("org.jetbrains.kotlin.multiplatform") version dokka_it_kotlin_version
        id("com.android.library") version dokka_it_android_gradle_plugin_version apply false
        id("com.android.application") version dokka_it_android_gradle_plugin_version apply false
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.dokka") {
                useModule("org.jetbrains.dokka:dokka-gradle-plugin:for-integration-tests-SNAPSHOT")
            }
        }
    }
    repositories {
        mavenLocal()
        maven("https://cache-redirector.jetbrains.com/jcenter.bintray.com")
        mavenCentral()
        gradlePluginPortal()
        google()
        maven("https://cache-redirector.jetbrains.com/dl.bintray.com/kotlin/kotlin-eap")
        maven("https://cache-redirector.jetbrains.com/dl.bintray.com/kotlin/kotlin-dev")
    }
}
