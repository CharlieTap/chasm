import kotlinx.benchmark.gradle.NativeSourceGeneratorTask
import org.gradle.kotlin.dsl.withType
import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask

plugins {
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.benchmark)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.kotlinx.test.resources)
}

val isMacOsArm64Host =
    System.getProperty("os.name") == "Mac OS X" &&
        System.getProperty("os.arch") in setOf("aarch64", "arm64")

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("kotlinx.benchmark.State")
}

benchmark {
    targets {
        register("jvm")
        if (isMacOsArm64Host) {
            register("macosArm64")
        }
    }
}

kotlin {

    jvm()
    macosArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.chasm)
                implementation(projects.executor.invoker)
                implementation(projects.memory)
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.runtime)

                implementation(libs.kotlinx.benchmark)
                implementation(libs.kotlinx.test.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

tasks.register<JavaExec>("coremark") {
    group = "benchmark"
    description = "Run the Coremark benchmark"
    classpath = kotlin.jvm().compilations["main"].run { runtimeDependencyFiles + output.allOutputs }
    mainClass.set("io.github.charlietap.chasm.benchmark.coremark.CoremarkBenchmarkKt")
}

tasks.withType<ConfigurableKtLintTask>().configureEach {
    dependsOn(tasks.withType<NativeSourceGeneratorTask>())
}
