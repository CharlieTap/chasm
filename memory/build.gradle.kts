import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)

    alias(libs.plugins.bolt)
}

bolt {
    library = "liblinmem"
    url = "https://github.com/CharlieTap/linmem/releases/download/0.1.48/"
    linkerOptions = mapOf(
        "mingw_x64" to "-lntdll"
    )
}

kotlin {
    sourceSets {
       commonMain {
            dependencies {
                api(projects.runtime.core)
                api(libs.result)
            }
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
    name = "memory"
    description = "a kotlin multiplatform wasm linear memory implementation"
}
