import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
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
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.android.library.bytecode.version.get()))
        }
    }
}

tasks.register("test") {
    group = "verification"
    description = "Run JVM tests for the fast development loop"
    dependsOn(tasks.named("jvmTest"))
}
