plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradle.versions.plugin)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }
}
