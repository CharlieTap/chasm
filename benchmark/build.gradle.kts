plugins {
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.benchmark)

    alias(libs.plugins.conventions.kmp)
    //alias(libs.plugins.conventions.linting)
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
                implementation(projects.executor.invoker)
                implementation(projects.executor.memory)
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.executor.runtime)

                implementation(libs.kotlinx.benchmark)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
