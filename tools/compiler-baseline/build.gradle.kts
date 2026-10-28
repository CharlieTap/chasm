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
        implementation(projects.cir)
        implementation(projects.compiler.backendChasm)
        implementation(projects.compiler.frontend)
        implementation(projects.config)
        implementation(projects.decoder)
        implementation(projects.executor.instantiator)
        implementation(projects.executor.invoker)
        implementation(projects.module)
        implementation(projects.runtime.address)
        implementation(projects.runtime.core)
        implementation(projects.runtime.resources)
        implementation(projects.runtime.type)
        implementation(projects.runtime.value)
        implementation(libs.kotlin.reflection)
        implementation(libs.kotlinx.serialization)
        implementation(libs.result)

        testImplementation(projects.test.fixture.cir)
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
    val generated = layout.buildDirectory.file("baselines/compiler/$name.json")
    val baseline = compilerBaselineDirectory.file("$name.json")
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
    name = "coremark-frontend",
    displayName = "CoreMark frontend",
    generatorMainClass = "io.github.charlietap.chasm.tools.compilerbaseline.GenerateCoremarkFrontendBaselineKt",
)
registerCompilerBaseline(
    name = "coremark-backend",
    displayName = "CoreMark backend",
    generatorMainClass = "io.github.charlietap.chasm.tools.compilerbaseline.GenerateCoremarkBackendBaselineKt",
)
registerCompilerBaseline(
    name = "contrived-frontend",
    displayName = "contrived frontend",
    generatorMainClass = "io.github.charlietap.chasm.tools.compilerbaseline.GenerateContrivedFrontendBaselineKt",
)
registerCompilerBaseline(
    name = "contrived-backend",
    displayName = "contrived backend",
    generatorMainClass = "io.github.charlietap.chasm.tools.compilerbaseline.GenerateContrivedBackendBaselineKt",
)
