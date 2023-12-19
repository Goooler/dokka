/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            name = "mavenCentral"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = providers.systemProperty("DOKKA_SONATYPE_USER").orNull
                password = providers.systemProperty("DOKKA_SONATYPE_PASSWORD").orNull
            }
        }
        maven {
            name = "spaceDev"
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
            credentials {
                username = providers.systemProperty("DOKKA_SPACE_PACKAGES_USER").orNull
                password = providers.systemProperty("DOKKA_SPACE_PACKAGES_SECRET").orNull
            }
        }
        maven {
            name = "spaceTest"
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/dokka/test")
            credentials {
                username = providers.systemProperty("DOKKA_SPACE_PACKAGES_USER").orNull
                password = providers.systemProperty("DOKKA_SPACE_PACKAGES_SECRET").orNull
            }
        }
        // Publish to a project-local Maven directory, for verification. To test, run:
        // ./gradlew publishAllPublicationsToProjectLocalRepository
        // and check $rootDir/build/maven-project-local
        maven {
            name = "projectLocal"
            url = uri(rootProject.layout.buildDirectory.dir("maven-project-local"))
        }
    }

    publications.withType<MavenPublication>().configureEach {
        pom {
            name.convention("Dokka ${project.name}")
            description.convention("Dokka is an API documentation engine for Kotlin")
            url.convention("https://github.com/Kotlin/dokka")

            licenses {
                license {
                    name.convention("The Apache Software License, Version 2.0")
                    url.convention("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.convention("repo")
                }
            }

            developers {
                developer {
                    id.convention("JetBrains")
                    name.convention("JetBrains Team")
                    organization.convention("JetBrains")
                    organizationUrl.convention("https://www.jetbrains.com")
                }
            }

            scm {
                connection.convention("scm:git:git://github.com/Kotlin/dokka.git")
                url.convention("https://github.com/Kotlin/dokka")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        providers.systemProperty("DOKKA_SIGN_KEY_ID").orNull?.takeIf(String::isNotBlank),
        providers.systemProperty("DOKKA_SIGN_KEY").orNull?.takeIf(String::isNotBlank),
        providers.systemProperty("DOKKA_SIGN_KEY_PASSPHRASE").orNull?.takeIf(String::isNotBlank),
    )
    sign(publishing.publications)
    setRequired(provider { !project.version.toString().endsWith("-SNAPSHOT") })
}

// This is a hack for a Gradle 8 problem, see https://github.com/gradle/gradle/issues/26091
//
// Fails with the following error otherwise:
// > Task ':runner-gradle-plugin-classic:publishDokkaPluginMarkerMavenPublicationToSpaceTestRepository' uses
// > this output of task ':runner-gradle-plugin-classic:signPluginMavenPublication' without declaring an
// > explicit or implicit dependency.
tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}
