import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.metro)

    alias(libs.plugins.conventions.linting)
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.consumerMultiplatform)
            }
        }
    }
}

val copyWasmBinaries by tasks.registering(Copy::class) {
    val resources = project(":consumer-multiplatform").layout.projectDirectory.dir("src/commonMain/resources")
    from(resources) {
        include("*.wasm")
    }
    into(layout.buildDirectory.dir("wasm"))
}

kotlin.sourceSets.named("commonMain") {
    resources.srcDir(layout.buildDirectory.dir("wasm"))
}

tasks.matching {
    (it.name.startsWith("js") || it.name.startsWith("wasmJs")) && it.name.contains("ProcessResources")
}.configureEach {
    dependsOn(copyWasmBinaries)
}
