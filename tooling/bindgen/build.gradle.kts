import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)

    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

configure<PublishingConventionsExtension> {
    name = "tooling-bindgen"
    description = "Generates a typesafe Kotlin interface, implementation and factory from a wasm binary"
}

mavenPublishing {
    coordinates(artifactId = "tooling-bindgen")
}

kotlinConventions {
    jvmBytecodeVersion.set(libs.versions.java.plugin.bytecode.version.map(String::toInt))
}

kotlin {
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    dependencies {
        api(projects.embeddingApi)
        api(libs.kotlin.poet)

        testImplementation(libs.kotlin.test)
        testImplementation(libs.kotlinx.test.resources)
        testImplementation(projects.test.fixture.chasm)
    }
}
