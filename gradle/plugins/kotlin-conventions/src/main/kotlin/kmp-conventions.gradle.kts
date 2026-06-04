
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink

plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.multiplatform")
}

fun KotlinMultiplatformExtension.unixTargets() = setOf(
    macosArm64 {
        binaries {
            test(listOf(RELEASE)) {

            }
        }
    },
    macosX64(),
    iosArm64(),
    iosSimulatorArm64(),
    iosX64(),
    linuxArm64(),
    linuxX64(),
)

fun KotlinMultiplatformExtension.nativeTargets() = setOf(
    mingwX64()
) + unixTargets()

kotlin {
    jvm()
    nativeTargets()
}

tasks.withType<KotlinNativeLink>().configureEach {
    if (name.endsWith("DebugTestMingwX64")) {
        binary.linkerOpts("-Wl,--stack,33554432")
    }
}
