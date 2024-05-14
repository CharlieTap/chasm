package io.github.charlietap.sweet.plugin.action

import io.github.charlietap.sweet.plugin.spec.testFileSpec
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

interface GenerateTestParams: WorkParameters {
    val runner: Property<String>
    val testPackage: Property<String>
    val scriptFile: RegularFileProperty
    val testFile: RegularFileProperty
}

abstract class GenerateTestAction : WorkAction<GenerateTestParams> {

    override fun execute() {

        val fileSpec = testFileSpec(
            runner = parameters.runner.get(),
            script = parameters.scriptFile.get().asFile,
            test = parameters.testFile.get().asFile,
            testPackage = parameters.testPackage.get(),
        )

        val testFile = parameters.testFile.get().asFile

        testFile.parentFile.mkdirs()
        testFile.createNewFile()

        parameters.testFile.get().asFile.writeText(fileSpec.toString())
    }
}
