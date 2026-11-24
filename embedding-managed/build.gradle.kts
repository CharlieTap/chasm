plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.embedding.managed"
    }

    compilerOptions {
        optIn.add("io.github.charlietap.chasm.InternalChasmApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.embeddingCompiled)

                implementation(projects.config)
                implementation(projects.embeddingRuntime)
                implementation(projects.runtime.compiled)
                implementation(projects.runtime.core)

                implementation(libs.result)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.runtime)
                implementation(projects.test.fixture.typeSystem)

                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "embedding-managed"
    description = "The engine behind stores whose GC objects are managed by the host VM, shared by Chasm's compiled tiers"
}
