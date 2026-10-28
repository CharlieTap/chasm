plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.cir"
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(projects.test.fixture.cir)

                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "cir"
    description = "chasms intermediate representation of compiled functions"
}
