import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    // AGP only creates the KMP Android lint tasks when this plugin is applied.
    id("com.android.lint")
}

val libs = the<LibrariesForLibs>()

fun KotlinMultiplatformExtension.unixTargets() = setOf(
    macosArm64 {
        binaries {
            test(listOf(RELEASE)) {

            }
        }
    },
    iosArm64(),
    iosSimulatorArm64(),
    linuxArm64(),
    linuxX64(),
)

fun KotlinMultiplatformExtension.nativeTargets() = setOf(
    mingwX64()
) + unixTargets()

kotlin {
    jvm()
    nativeTargets()

    android {
        compileSdk = libs.versions.compile.sdk.get().toInt()
        minSdk = libs.versions.min.sdk.get().toInt()
        withHostTest {}
        lint {
            checkOnly += "NewApi"
            abortOnError = true
        }
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.android.library.bytecode.version.get()))
        }
    }
}

// com.android.lint also lints the JVM target as part of check, with all
// default checks on. Limit that to NewApi too, so check stays offline and quiet.
extensions.configure<com.android.build.api.dsl.Lint>("lint") {
    checkOnly += "NewApi"
}

tasks.register("test") {
    group = "verification"
    description = "Run JVM tests for the fast development loop"
    dependsOn(tasks.named("jvmTest"))
}
