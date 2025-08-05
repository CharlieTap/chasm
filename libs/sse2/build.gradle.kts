import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
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
       commonMain {
            dependencies {}
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

tasks.withType<KotlinNativeCompile>().configureEach {
    compilerOptions {
        optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
    }
}

configure<PublishingConventionsExtension> {
    name = "numeric"
    description = "numeric operations for chasms runtime execution"
}
