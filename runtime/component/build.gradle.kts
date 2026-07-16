plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.config)
                api(projects.runtime.address)
                api(projects.runtime.core)
                api(projects.runtime.value)
                api(projects.typeSystem)
                api(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.runtime.component)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-component"
    description = "component model runtime state and canonical ABI metadata"
}
