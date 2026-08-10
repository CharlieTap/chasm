plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {

        all {
            languageSettings {

            }
        }

       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.config)
                api(projects.runtime.core)

                implementation(projects.executor.invoker)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.config)
                implementation(projects.test.fixture.runtime)

                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "compiler"
    description = "bytecode compiler for chasm modules"
}
