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

    sourceSets {
        jsMain {
            dependencies {
                implementation(projects.consumerMultiplatformFactorial)
            }
        }
    }
}

val copyWasmBinaries by tasks.registering(Copy::class) {
    val resources = project(":consumer-multiplatform-factorial").layout.projectDirectory.dir("src/commonMain/resources")
    from(resources) {
        include("*.wasm")
    }
    into(layout.buildDirectory.dir("wasm"))
}

kotlin.sourceSets.named("jsMain") {
    resources.srcDir(layout.buildDirectory.dir("wasm"))
}

tasks.matching { it.name.startsWith("js") && it.name.contains("ProcessResources") }.configureEach {
    dependsOn(copyWasmBinaries)
}
