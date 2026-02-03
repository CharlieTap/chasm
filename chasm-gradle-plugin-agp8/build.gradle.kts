import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)
    `kotlin-dsl`

    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

group = "io.github.charlietap.chasm"
version = libs.versions.plugin.version.name.get()

configure<PublishingConventionsExtension> {
    name = "chasm-gradle-plugin-agp8"
    description = "AGP 8 integration for the chasm Gradle plugin"
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }

    dependencies {
        implementation(projects.chasmGradlePluginCompat)
        implementation(projects.chasmGradlePluginCodegen)
        compileOnly(libs.android.gradle.plugin8)

        testImplementation(libs.kotlin.test)
        testImplementation(libs.android.gradle.plugin8)
        testImplementation(gradleTestKit())
    }
}
