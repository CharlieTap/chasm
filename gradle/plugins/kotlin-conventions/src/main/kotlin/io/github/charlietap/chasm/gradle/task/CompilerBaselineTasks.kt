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
        val baseline = baselineFile.get().asFile
        val generated = generatedFile.get().asFile
        if (baseline.readBytes().contentEquals(generated.readBytes())) return

        throw GradleException(
            buildString {
                appendLine("Compiler output differs from ${baseline.name}. Run updateCompilerBaseline to accept the new output.")
                appendLine("Generated output: ${generated.path}")
                append(changedLines(baseline.readLines(), generated.readLines()))
            },
        )
    }
}

private fun changedLines(expected: List<String>, actual: List<String>): String {
    var start = 0
    while (start < expected.size && start < actual.size && expected[start] == actual[start]) start++
    var expectedEnd = expected.size
    var actualEnd = actual.size
    while (expectedEnd > start && actualEnd > start && expected[expectedEnd - 1] == actual[actualEnd - 1]) {
        expectedEnd--
        actualEnd--
    }
    val name = expected.subList(0, start)
        .lastOrNull { line -> line.trimStart().startsWith(NAME_KEY) }
        ?.trim()
        ?.removePrefix(NAME_KEY)
        ?.removeSuffix(",")
    return buildString {
        appendLine("First difference at line ${start + 1}${name?.let { " in $it" } ?: ""}:")
        expected.subList(start, expectedEnd).take(CHANGED_LINE_LIMIT).forEach { line -> appendLine("- ${line.trim()}") }
        actual.subList(start, actualEnd).take(CHANGED_LINE_LIMIT).forEach { line -> appendLine("+ ${line.trim()}") }
        val omitted = maxOf(expectedEnd - start - CHANGED_LINE_LIMIT, 0) + maxOf(actualEnd - start - CHANGED_LINE_LIMIT, 0)
        if (omitted > 0) appendLine("... $omitted more changed lines")
    }
}

private const val CHANGED_LINE_LIMIT = 20
private const val NAME_KEY = "\"name\": "
