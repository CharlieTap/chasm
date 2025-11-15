plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.benchmark) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.chasm) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.metro) apply false


    alias(libs.plugins.conventions.linting) apply false
}

tasks.register("fmt") {
    group = "formatting"
    description = "Format sources"

    val lintingTasks = subprojects.mapNotNull { it.tasks.findByName("formatKotlin") }

    dependsOn(lintingTasks)
}
