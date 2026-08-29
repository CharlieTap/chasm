
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.konan.target.HostManager

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
    if (HostManager.hostIsSupported) {
        nativeTargets()
    }
}

tasks.register("test") {
    group = "verification"
    description = "Run JVM tests for the fast development loop"
    dependsOn(tasks.named("jvmTest"))
}
