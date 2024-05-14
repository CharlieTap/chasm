plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
        vendor.set(JvmVendorSpec.matching(libs.versions.java.vendor.get()))
    }

    dependencies {
        implementation(libs.kotlin.gradle.plugin)
        implementation(libs.kotlinx.serialization)
        implementation(libs.kotlin.poet)
    }

}

gradlePlugin {
    plugins {
        create("sweet-plugin") {
            id = "io.github.charlietap.sweet.plugin"
            implementationClass = "io.github.charlietap.sweet.plugin.WasmTestSuiteGenPlugin"
        }
    }
}
