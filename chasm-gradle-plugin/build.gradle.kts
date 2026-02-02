import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.build.config)
    `kotlin-dsl`
    `java-gradle-plugin`

    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

group = "io.github.charlietap.chasm"
version = libs.versions.plugin.version.name.get()

buildConfig {
    buildConfigField("RUNTIME_VERSION", libs.versions.version.name.get())
}

configure<PublishingConventionsExtension> {
    name = "chasm-gradle-plugin"
    description = "A gradle plugin for generating a typesafe Kotlin interface from a wasm binary"
}

gradlePlugin {
    plugins {
        create("chasm-gradle-plugin") {
            id = "io.github.charlietap.chasm.gradle"
            implementationClass = "io.github.charlietap.chasm.gradle.ChasmPlugin"
        }
    }
}

kotlin {

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled.set(true)
    }

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }

    dependencies {
        api(projects.chasmGradlePluginApi)
        api(projects.chasmGradlePluginCodegen)
        implementation(projects.chasmGradlePluginCompat)
        implementation(projects.chasmGradlePluginAgp8)
        implementation(projects.chasmGradlePluginAgp9)

        implementation(libs.kotlin.gradle.plugin)
    }
}

