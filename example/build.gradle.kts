plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.symbol.processing) apply false
}

tasks.register("clean",Delete::class){
    delete(rootProject.layout.buildDirectory)
}
