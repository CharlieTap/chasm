import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.build.config)

    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

configure<PublishingConventionsExtension> {
    name = "chasm-aot"
    description = "Compiles wasm modules ahead of time to JVM classes"
}

kotlinConventions {
    jvmBytecodeVersion.set(libs.versions.java.plugin.bytecode.version.map(String::toInt))
}

buildConfig {
    packageName("io.github.charlietap.chasm.aot")
    buildConfigField("VERSION", version.toString())
}

kotlin {
    jvm()

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)
                api(projects.runtime.compiledApi)

                implementation(projects.ast)
                implementation(projects.moduleDeclarations)
                implementation(projects.compiler.frontend)
                implementation(projects.compiler.backendJvm)
                implementation(projects.decoder)
                implementation(projects.module)
                implementation(projects.stream)
                implementation(projects.validator)
                implementation(libs.result)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.embeddingModule)
                implementation(projects.runtime.compiled)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

tasks.register("test") {
    group = "verification"
    description = "Run JVM tests for the fast development loop"
    dependsOn(tasks.named("jvmTest"))
}
