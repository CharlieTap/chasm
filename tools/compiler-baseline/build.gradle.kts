import io.github.charlietap.chasm.gradle.task.CheckCompilerBaselineTask
import io.github.charlietap.chasm.gradle.task.GenerateCompilerBaselineTask
import io.github.charlietap.chasm.gradle.task.UpdateCompilerBaselineTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    dependencies {
        implementation(projects.ast)
        implementation(projects.compiler)
        implementation(projects.config)
        implementation(projects.decoder)
        implementation(projects.executor.instantiator)
        implementation(projects.runtime.core)
        implementation(libs.kotlinx.serialization)
        implementation(libs.result)

        testImplementation(libs.kotlin.test)
    }
}

val compilerBaselineDirectory = layout.projectDirectory.dir("../../baselines/compiler")
val compilerBaselineRuntimeClasspath = sourceSets.main.get().runtimeClasspath

val compilerBaseline = tasks.register("compilerBaseline") {
    group = "verification"
    description = "Generate the compiler baselines"
}

val updateCompilerBaseline = tasks.register("updateCompilerBaseline") {
    group = "verification"
    description = "Update the checked-in compiler baselines"
}

val checkCompilerBaseline = tasks.register("checkCompilerBaseline") {
    group = "verification"
    description = "Check the compiler baselines"
}

fun registerCompilerBaseline(
    name: String,
    displayName: String,
    generatorMainClass: String,
) {
    val taskName = name.replaceFirstChar(Char::uppercase)
    val generated = layout.buildDirectory.file("baselines/compiler/$name-baseline.json")
    val baseline = compilerBaselineDirectory.file("$name-baseline.json")
    val generate = tasks.register<GenerateCompilerBaselineTask>("generate${taskName}CompilerBaseline") {
        group = "verification"
        description = "Generate the $displayName compiler baseline"
        runtimeClasspath.from(compilerBaselineRuntimeClasspath)
        outputFile.set(generated)
        mainClass.set(generatorMainClass)
    }
    val update = tasks.register<UpdateCompilerBaselineTask>("update${taskName}CompilerBaseline") {
        group = "verification"
        description = "Update the checked-in $displayName compiler baseline"
        dependsOn(generate)
        generatedFile.set(generated)
        baselineFile.set(baseline)
    }
    val check = tasks.register<CheckCompilerBaselineTask>("check${taskName}CompilerBaseline") {
        group = "verification"
        description = "Check the $displayName compiler baseline"
        dependsOn(generate)
        mustRunAfter(update)
        baselineFile.set(baseline)
        generatedFile.set(generated)
    }

    compilerBaseline.configure { dependsOn(generate) }
    updateCompilerBaseline.configure { dependsOn(update) }
    checkCompilerBaseline.configure { dependsOn(check) }
}

registerCompilerBaseline(
    name = "coremark",
    displayName = "CoreMark",
    generatorMainClass = "io.github.charlietap.chasm.tools.compilerbaseline.GenerateCompilerBaselineKt",
)
registerCompilerBaseline(
    name = "contrived",
    displayName = "contrived",
    generatorMainClass = "io.github.charlietap.chasm.tools.compilerbaseline.GenerateContrivedCompilerBaselineKt",
)
