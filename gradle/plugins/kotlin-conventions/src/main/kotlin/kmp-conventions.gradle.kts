
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink

plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.multiplatform")
}

@Suppress("DEPRECATION")
fun KotlinMultiplatformExtension.compatibilityMacosX64() = macosX64()

fun KotlinMultiplatformExtension.unixTargets() = setOf(
    macosArm64 {
        binaries {
            test(listOf(RELEASE)) {

            }
        }
    },
    compatibilityMacosX64(),
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

tasks.register("test") {
    group = "verification"
    description = "Run JVM tests for the fast development loop"
    dependsOn(tasks.named("jvmTest"))
}

tasks.withType<KotlinNativeLink>().configureEach {
    if (name.endsWith("DebugTestMingwX64")) {
        binary.linkerOpts("-Wl,--stack,33554432")
    }
}
