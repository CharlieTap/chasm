import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import org.jetbrains.kotlin.konan.target.Family

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.sse2"
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("nonWindows") {
                withCompilations {
                    (it.target as? KotlinNativeTarget)?.konanTarget?.family != Family.MINGW
                }
            }
        }
    }

    mingwX64 {
        compilations.getByName("main") {
            cinterops {
                create("libsse2") {
                    defFile(project.file("src/cinterop/libsse2.def"))
                }
            }
        }
    }

    sourceSets {
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
