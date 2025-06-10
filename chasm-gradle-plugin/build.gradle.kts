plugins {
    alias(libs.plugins.kotlin.jvm)
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "io.github.charlietap.chasm"
version = "0.1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

gradlePlugin {
    plugins {
        create("chasm-gradle-plugin") {
            id = "io.github.charlietap.chasm.gradle"
            implementationClass = "io.github.charlietap.chasm.gradle.ChasmPlugin"
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }

    dependencies {
        implementation(projects.chasm)

        implementation(libs.kotlin.gradle.plugin)
        implementation(libs.kotlin.poet)

        testImplementation(libs.kotlin.test)
        testImplementation(projects.test.fixture.chasm)
    }
}

