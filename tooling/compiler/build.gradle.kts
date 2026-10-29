import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)

    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

configure<PublishingConventionsExtension> {
    name = "tooling-compiler"
    description = "Compiles wasm modules ahead of time to JVM classes, and reads what a module imports and exports"
}

mavenPublishing {
    coordinates(artifactId = "tooling-compiler")
}

kotlinConventions {
    jvmBytecodeVersion.set(libs.versions.java.plugin.bytecode.version.map(String::toInt))
}

kotlin {
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    dependencies {
        api(projects.config)
        api(projects.embeddingApi)

        implementation(projects.ast)
        implementation(projects.moduleDeclarations)
        implementation(projects.compiler.frontend)
        implementation(projects.compiler.backendJvm)
        implementation(projects.decoder)
        implementation(projects.module)
        implementation(projects.stream)
        implementation(projects.validator)
        implementation(libs.result)

        testImplementation(libs.kotlin.test)
    }
}
