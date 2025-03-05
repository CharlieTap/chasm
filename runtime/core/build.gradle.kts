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
                api(projects.ir)
                api(projects.libs.stack)
                api(libs.result)
                api(projects.runtime.type)
                api(projects.runtime.value)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-core"
    description = "chasms runtime implementation"
}
