import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)

    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

group = "io.github.charlietap.chasm"
version = libs.versions.plugin.version.name.get()

configure<PublishingConventionsExtension> {
    name = "chasm-gradle-plugin-agp9"
    description = "AGP 9 integration for the chasm Gradle plugin"
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }

    dependencies {
        implementation(projects.chasmGradlePluginCompat)
        implementation(projects.chasmGradlePluginCodegen)
        compileOnly(gradleApi())
        compileOnly(libs.android.gradle.plugin)

        testImplementation(libs.kotlin.test)
        testImplementation(libs.android.gradle.plugin)
        testImplementation(gradleTestKit())
    }
}
