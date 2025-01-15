

plugins {
    alias(libs.plugins.kotlin.allopen) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.atomic.fu) apply false
    alias(libs.plugins.kotlin.benchmark) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.symbol.processing) apply false
    alias(libs.plugins.bolt) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.gradle.maven.publish) apply false
    alias(libs.plugins.wasm.testsuite) apply false

    alias(libs.plugins.conventions.kmp) apply false
    alias(libs.plugins.conventions.publishing) apply false
    alias(libs.plugins.conventions.linting) apply false
    alias(libs.plugins.conventions.versions)
}

tasks.register("clean",Delete::class){
    delete(rootProject.layout.buildDirectory)
}
