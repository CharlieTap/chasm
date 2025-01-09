plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(projects.ast)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.executor.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-external"
    description = "execution stack values for chasms runtime"
}
