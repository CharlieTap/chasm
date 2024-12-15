import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

group = "io.github.charlietap.sweet"


kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(libs.kotlinx.serialization)
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.java.bytecode.version.get())
    }
}
