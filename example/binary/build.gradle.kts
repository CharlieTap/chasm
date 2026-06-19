import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.conventions.linting)
}

kotlin {
    jvm()
    js {
        nodejs()
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
}

