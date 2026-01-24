package io.github.charlietap.sweet.plugin.action

import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.LimitedSupport
import io.github.charlietap.sweet.plugin.Proposal
import io.github.charlietap.sweet.plugin.spec.testFileSpec
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Paths

interface GenerateTestParams: WorkParameters {
    val proposal: ListProperty<Proposal>
    val limited: ListProperty<LimitedSupport>
    val runner: Property<String>
    val testPackage: Property<String>
    val scriptFile: RegularFileProperty
    val testFile: RegularFileProperty
}

abstract class GenerateTestAction : WorkAction<GenerateTestParams> {

    override fun execute() {

        val filePath = parameters.testFile.get().asFile.toString()
        val scriptFile = parameters.scriptFile.get().asFile

        val phaseSupport = parameters.proposal.get().firstOrNull {
            filePath.contains("proposal${File.separator}${it.name}")
        }?.phaseSupport ?: parameters.limited.get().firstOrNull { limited ->
            limited.patterns.any { pattern -> matchesGlobPattern(scriptFile, pattern) }
        }?.phaseSupport ?: SemanticPhase.EXECUTION

        val fileSpec = testFileSpec(
            phaseSupport = phaseSupport,
            runner = parameters.runner.get(),
            script = scriptFile,
            test = parameters.testFile.get().asFile,
            testPackage = parameters.testPackage.get(),
        )

        val testFile = parameters.testFile.get().asFile

        testFile.parentFile.mkdirs()
        testFile.createNewFile()

        parameters.testFile.get().asFile.writeText(fileSpec.toString())
    }

    private fun matchesGlobPattern(file: File, pattern: String): Boolean {
        // The intermediate file structure is: originalName/originalName.json
        // So the parent directory name is the original .wast file name without extension
        val originalBaseName = file.parentFile?.name ?: file.nameWithoutExtension

        val normalizedPattern = pattern
            .removeSuffix(".wast")
            .removePrefix("**/")
            .removePrefix("*/")

        val matcher = FileSystems.getDefault().getPathMatcher("glob:$normalizedPattern")

        return matcher.matches(Paths.get(originalBaseName))
    }
}
