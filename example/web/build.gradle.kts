import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.metro)

    alias(libs.plugins.conventions.linting)
}

val wasmResourcesDependencies = configurations.dependencyScope("wasmResourcesDependencies")
val wasmResources = configurations.resolvable("wasmResources") {
    extendsFrom(wasmResourcesDependencies.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named("chasm-wasm"))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("resources"))
    }
}

dependencies {
    add(wasmResourcesDependencies.name, projects.consumerMultiplatform)
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

val copyWasmBinaries = tasks.register<Copy>("copyWasmBinaries") {
    from(wasmResources) {
        include("*.wasm")
    }
    into(layout.buildDirectory.dir("wasm"))
}

kotlin.sourceSets.named("commonMain") {
    resources.srcDir(copyWasmBinaries.map { it.destinationDir })
}
