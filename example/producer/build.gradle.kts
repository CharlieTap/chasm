@file:OptIn(ExperimentalWasmDsl::class)

import io.github.charlietap.chasm.gradle.Mode
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.symbol.processing)
    alias(libs.plugins.chasm)

    alias(libs.plugins.conventions.linting)
}

kotlin {
    jvm()
    wasmWasi {
        binaries.executable()
    }
    iosArm64()

    sourceSets {
        jvmMain.dependencies {
            implementation(libs.chasm.jvm)
            implementation(libs.hilt.core)
        }
    }

    dependencies {
        add("kspJvm", libs.hilt.compiler)
    }
}

chasm {
    mode = Mode.PRODUCER
    modules {
        create("ProducerService") {
            packageName = "com.test.chasm.producer"
            initializers = setOf("_initialize")
        }
    }
}
