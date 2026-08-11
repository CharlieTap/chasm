plugins {
    alias(libs.plugins.kotlin.allopen) apply false
    alias(libs.plugins.kotlin.atomic.fu) apply false
    alias(libs.plugins.kotlin.benchmark) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.gradle.maven.publish) apply false
    alias(libs.plugins.wasm.testsuite) apply false
    alias(libs.plugins.wasm.corpus) apply false

    alias(libs.plugins.conventions.kmp) apply false
    alias(libs.plugins.conventions.kotlin) apply false
    alias(libs.plugins.conventions.gradle.plugin) apply false
    alias(libs.plugins.conventions.publishing) apply false
    alias(libs.plugins.conventions.linting) apply false
    alias(libs.plugins.conventions.versions)
}

tasks.register("fmt") {
    group = "formatting"
    description = "Format sources"

    dependsOn(
        gradle.includedBuild("corpus").task(":fmt"),
        gradle.includedBuild("sweet").task(":fmt"),
    )
}
