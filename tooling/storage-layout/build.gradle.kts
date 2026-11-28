plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.storage.layout"
    }

    validatePublicAbi()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.embeddingApi)
                api(projects.typeSystem)

                implementation(projects.compiler.backendAnalysis)
                implementation(projects.decoder)
                implementation(projects.runtime.compiledFormat)
                implementation(projects.validator)
                implementation(libs.result)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "tooling-storage-layout"
    description = "Tells code generators which storage class and slots Chasm's compiled runtime uses for each struct and array type"
}
