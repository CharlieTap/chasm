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
    name = "chasm-gradle-plugin-compat"
    description = "Compatibility surface for the chasm Gradle plugin"
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }

    dependencies {
        api(projects.chasmGradlePluginApi)
        testImplementation(libs.kotlin.test)
    }
}
