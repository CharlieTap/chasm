plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.jna)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "benchmark"
    description = "benchmark placement controls and validation"
}

tasks.withType<Test>().configureEach {
    if (JavaVersion.current() >= JavaVersion.VERSION_24) {
        jvmArgs("--enable-native-access=ALL-UNNAMED")
    }
}
