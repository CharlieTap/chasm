plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.compiler.backend.dex"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ast)
                api(projects.cir)
                api(projects.compiler.backendAbi)
                implementation(projects.compiler.backendAnalysis)

                implementation(projects.runtime.core)
                implementation(libs.dexter)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.compiler.frontend)
                implementation(projects.decoder)
                implementation(projects.validator)
                implementation(libs.kotlin.test)
                implementation(libs.result)
                implementation(libs.smali.baksmali)

                runtimeOnly(projects.runtime.compiled)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    maxHeapSize = "2g"
    providers.systemPropertiesPrefixedBy("codegen.").get().forEach { (key, value) -> systemProperty(key, value) }
}

configure<PublishingConventionsExtension> {
    name = "compiler-backend-dex"
    description = "Lowers Chasm IR to DEX bytecode for Android"
}
