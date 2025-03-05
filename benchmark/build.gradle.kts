plugins {
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.benchmark)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("kotlinx.benchmark.State")
}

benchmark {
    targets {
        register("jvm")
        register("macosArm64")
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
                implementation(libs.kotlinx.datetime)
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
