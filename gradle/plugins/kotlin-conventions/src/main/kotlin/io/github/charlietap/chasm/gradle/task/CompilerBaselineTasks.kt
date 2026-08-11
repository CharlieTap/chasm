package io.github.charlietap.chasm.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@CacheableTask
abstract class GenerateCompilerBaselineTask @Inject constructor(
    private val execOperations: ExecOperations,
) : DefaultTask() {

    @get:Classpath
    abstract val runtimeClasspath: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val mainClass: Property<String>

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
