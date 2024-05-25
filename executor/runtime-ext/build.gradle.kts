import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
                api(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.executor.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-ext"
    description = "Internal extensions for chasms runtime"
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.java.bytecode.version.get())
    }
}
