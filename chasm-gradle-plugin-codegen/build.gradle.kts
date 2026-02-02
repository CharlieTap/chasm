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
    name = "chasm-gradle-plugin-codegen"
    description = "Codegen implementation for the chasm Gradle plugin"
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
        implementation(projects.chasm)?.because("We use the module and moduleInfo calls during codegen")
        implementation(projects.vm)
        implementation(libs.kotlin.poet)

        testImplementation(libs.kotlin.test)
        testImplementation(projects.test.fixture.chasm)
    }
}
