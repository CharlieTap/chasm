import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@CacheableTask
abstract class GenerateCompilerBaselineTask : DefaultTask() {

    @get:Classpath
    abstract val runtimeClasspath: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val mainClass: Property<String>

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun generate() {
        val output = outputFile.get().asFile
        output.parentFile.mkdirs()
        output.outputStream().use { stream ->
            execOperations.javaexec {
                classpath(runtimeClasspath)
                mainClass.set(this@GenerateCompilerBaselineTask.mainClass)
                standardOutput = stream
            }
        }
    }
}

@DisableCachingByDefault(because = "Updates a checked-in baseline")
abstract class UpdateCompilerBaselineTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val generatedFile: RegularFileProperty

    @get:OutputFile
    abstract val baselineFile: RegularFileProperty

    @TaskAction
    fun update() {
        generatedFile.get().asFile.copyTo(
            target = baselineFile.get().asFile,
            overwrite = true,
        )
    }
}

@DisableCachingByDefault(because = "Baseline comparison has no outputs")
abstract class CheckCompilerBaselineTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val baselineFile: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val generatedFile: RegularFileProperty

    @TaskAction
    fun verify() {
        if (!baselineFile.get().asFile.readBytes().contentEquals(generatedFile.get().asFile.readBytes())) {
            throw GradleException(
                "Compiler output differs from the checked-in baseline. " +
                    "Run updateCompilerBaseline to accept the new output.",
            )
        }
    }
}

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

data class CompilerBaselineTasks(
    val generate: TaskProvider<GenerateCompilerBaselineTask>,
    val update: TaskProvider<UpdateCompilerBaselineTask>,
    val check: TaskProvider<CheckCompilerBaselineTask>,
)

fun registerCompilerBaseline(
    name: String,
    displayName: String,
    generatorMainClass: String,
): CompilerBaselineTasks {
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
        baselineFile.set(baseline)
        generatedFile.set(generated)
    }
    return CompilerBaselineTasks(generate, update, check)
}

val coremarkCompilerBaseline = registerCompilerBaseline(
    name = "coremark",
    displayName = "CoreMark",
    generatorMainClass = "io.github.charlietap.chasm.tools.compilerbaseline.GenerateCompilerBaselineKt",
)
val contrivedCompilerBaseline = registerCompilerBaseline(
    name = "contrived",
    displayName = "contrived",
    generatorMainClass = "io.github.charlietap.chasm.tools.compilerbaseline.GenerateContrivedCompilerBaselineKt",
)

tasks.register("compilerBaseline") {
    group = "verification"
    description = "Generate the compiler baselines"
    dependsOn(coremarkCompilerBaseline.generate, contrivedCompilerBaseline.generate)
}

tasks.register("updateCompilerBaseline") {
    group = "verification"
    description = "Update the checked-in compiler baselines"
    dependsOn(coremarkCompilerBaseline.update, contrivedCompilerBaseline.update)
}

tasks.register("checkCompilerBaseline") {
    group = "verification"
    description = "Check the compiler baselines"
    dependsOn(coremarkCompilerBaseline.check, contrivedCompilerBaseline.check)
}
