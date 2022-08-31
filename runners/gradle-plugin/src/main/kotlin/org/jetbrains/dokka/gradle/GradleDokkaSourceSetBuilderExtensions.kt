package org.jetbrains.dokka.gradle

import com.android.build.api.dsl.AndroidSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun GradleDokkaSourceSetBuilder.dependsOn(sourceSet: KotlinSourceSet) {
    dependsOn(DokkaSourceSetID(sourceSet.name))
}

fun GradleDokkaSourceSetBuilder.dependsOn(sourceSet: AndroidSourceSet) {
    dependsOn(DokkaSourceSetID(sourceSet.name))
}

fun GradleDokkaSourceSetBuilder.kotlinSourceSet(kotlinSourceSet: KotlinSourceSet) {
    configureWithKotlinSourceSet(kotlinSourceSet)
}

