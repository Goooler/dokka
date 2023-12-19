/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.9.10"
    id("org.jetbrains.dokka") version "1.9.10"
    `maven-publish`
    signing
}

group = "org.example"
version = "1.4-SNAPSHOT"

repositories {
    mavenCentral()
}

val dokkaVersion: String by project
dependencies {

    compileOnly("org.jetbrains.dokka:dokka-core:$dokkaVersion")
    implementation("org.jetbrains.dokka:dokka-base:$dokkaVersion")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.dokka:dokka-test-api:$dokkaVersion")
    testImplementation("org.jetbrains.dokka:dokka-base-test-utils:$dokkaVersion")
}

val dokkaOutputDir = "$buildDir/dokka"

tasks {
    withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
    }
    dokkaHtml {
        outputDirectory = file(dokkaOutputDir)
    }
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier = "javadoc"
    from(dokkaOutputDir)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        val dokkaTemplatePlugin by creating(MavenPublication::class) {
            artifactId = project.name
            from(components["java"])
            artifact(javadocJar.get())

            pom {
                name = "Dokka template plugin"
                description = "This is a plugin template for Dokka"
                url = "https://github.com/Kotlin/dokka-plugin-template/"

                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "repo"
                    }
                }

                developers {
                    developer {
                        id = "JetBrains"
                        name = "JetBrains Team"
                        organization = "JetBrains"
                        organizationUrl = "http://www.jetbrains.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Kotlin/dokka-plugin-template.git"
                    url = "https://github.com/Kotlin/dokka-plugin-template/tree/master"
                }
            }
        }
        signPublicationsIfKeyPresent(dokkaTemplatePlugin)
    }

    repositories {
        maven {
            url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = providers.systemProperty("SONATYPE_USER").orNull
                password = providers.systemProperty("SONATYPE_PASSWORD").orNull
            }
        }
    }
}

fun Project.signPublicationsIfKeyPresent(publication: MavenPublication) {
    val signingKeyId: String? = providers.systemProperty("SIGN_KEY_ID").orNull
    val signingKey: String? = providers.systemProperty("SIGN_KEY").orNull
    val signingKeyPassphrase: String? = providers.systemProperty("SIGN_KEY_PASSPHRASE").orNull

    if (!signingKey.isNullOrBlank()) {
        extensions.configure<SigningExtension>("signing") {
            if (signingKeyId?.isNotBlank() == true) {
                useInMemoryPgpKeys(signingKeyId, signingKey, signingKeyPassphrase)
            } else {
                useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
            }
            sign(publication)
        }
    }
}
