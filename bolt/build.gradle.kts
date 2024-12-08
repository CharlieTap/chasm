plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }

    dependencies {
        implementation(libs.kotlin.gradle.plugin)
    }
}

gradlePlugin {
    plugins {
        create("bolt-plugin") {
            id = "io.github.charlietap.bolt.plugin"
            implementationClass = "io.github.charlietap.bolt.plugin.BoltPlugin"
        }
    }
}
