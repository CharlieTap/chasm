plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.build.config)
    `kotlin-dsl`
    `java-gradle-plugin`

    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

group = "io.github.charlietap.chasm"
version = libs.versions.version.name.get()

buildConfig {
    buildConfigField("RUNTIME_VERSION", libs.versions.version.name.get())
}

configure<PublishingConventionsExtension> {
    name = "chasm-gradle-plugin"
    description = "A gradle plugin for generating a typesafe Kotlin interface from a wasm binary"
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
        implementation(libs.android.gradle.plugin)
        implementation(libs.kotlin.poet)

        testImplementation(libs.kotlin.test)
        testImplementation(projects.test.fixture.chasm)
    }
}

