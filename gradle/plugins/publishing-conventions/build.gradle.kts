plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.dokka.gradle.plugin)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }
}

gradlePlugin {
    plugins {
        create("publishingConventions") {
            id = "publishing-conventions"
            implementationClass = "PublishingConventionsPlugin"
        }
    }
}
