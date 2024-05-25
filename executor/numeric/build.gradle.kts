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

    mingwX64 {
        compilations.getByName("main") {
            cinterops {
                val libsse2 by creating {
                    defFile(project.file("src/cinterop/libsse2.def"))
                }
            }
        }
    }

    sourceSets {

        all {
            languageSettings {
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }

       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.executor.runtime)
                api(projects.executor.runtimeExt)
                api(projects.executor.type)
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
    name = "numeric"
    description = "numeric operations for chasms runtime execution"
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.java.bytecode.version.get())
    }
}
