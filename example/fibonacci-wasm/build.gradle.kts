import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("linting-conventions")
}

kotlin {

    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        binaries.executable()
    }

    sourceSets {
       commonMain {
            dependencies {

            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.java.bytecode.version.get()
}

tasks.withType<KotlinJsCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf("-Xwasm-use-traps-instead-of-exceptions")
}

