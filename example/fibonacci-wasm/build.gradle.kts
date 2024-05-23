import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.java.bytecode.version.get())
    }
}

tasks.withType<KotlinJsCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll("-Xwasm-use-traps-instead-of-exceptions")
    }
}
