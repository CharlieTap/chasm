package io.github.charlietap.sweet.plugin.action

import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.spec.testFileSpec
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

interface GenerateTestParams : WorkParameters {
    val phaseSupport: Property<SemanticPhase>
    val excludedLines: SetProperty<Int>
    val runner: Property<String>
    val testPackage: Property<String>
    val scriptFile: RegularFileProperty
    val testFile: RegularFileProperty
}

abstract class GenerateTestAction : WorkAction<GenerateTestParams> {

    override fun execute() {
        val testFile = parameters.testFile.get().asFile
        val fileSpec = testFileSpec(
            phaseSupport = parameters.phaseSupport.get(),
            excludedLines = parameters.excludedLines.get(),
            runner = parameters.runner.get(),
            script = parameters.scriptFile.get().asFile,
            test = testFile,
            testPackage = parameters.testPackage.get(),
        )

        testFile.parentFile.mkdirs()
        testFile.writeText(fileSpec.toString())
    }
}
