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

gradlePlugin {
    plugins {
        create("publishingConventions") {
            id = "publishing-conventions"
            implementationClass = "PublishingConventionsPlugin"
        }
    }
}
