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

        all {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }

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

configure<PublishingConventionsExtension> {
    name = "numeric"
    description = "numeric operations for chasms runtime execution"
}
