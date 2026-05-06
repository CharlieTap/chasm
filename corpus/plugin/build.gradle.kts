plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "io.github.charlietap.corpus"
version = libs.versions.plugin.version.name.get()

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }

    dependencies {
        api(projects.lib)
        implementation(libs.kotlin.gradle.plugin)
        implementation(libs.kotlinx.serialization)
        implementation(libs.kotlin.poet)
    }
}

gradlePlugin {
    plugins {
        create("corpus-plugin") {
            id = "io.github.charlietap.corpus.plugin"
            implementationClass = "io.github.charlietap.corpus.plugin.WasmCorpusPlugin"
        }
    }
}
