import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)
    id("kmp-conventions")
    id("linting-conventions")
    id("publishing-conventions")
}

kotlin {

    sourceSets {

        all {
            languageSettings {
                optIn("kotlin.ExperimentalUnsignedTypes")
            }
        }

       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.executor.runtime)
                api(projects.executor.runtimeExt)
                api(projects.executor.invoker)
                api(libs.result)

                implementation(projects.executor.memory)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.executor.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "executor-instantiator"
    description = "A wasm module instantiator"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.java.bytecode.version.get()
}
