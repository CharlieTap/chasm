import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    macosArm64()
    iosArm64()
    iosSimulatorArm64()
    linuxArm64()
    linuxX64()
    mingwX64()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val nativeMain = getByName("nativeMain")
        val appleMain = getByName("appleMain")
        val linuxMain = getByName("linuxMain")
        val unixMain = create("unixMain") {
            dependsOn(nativeMain)
        }

        appleMain.dependsOn(unixMain)
        linuxMain.dependsOn(unixMain)
    }
}

configure<PublishingConventionsExtension> {
    name = "mmap"
    description = "Anonymous virtual memory mapping for Kotlin Native"
}
