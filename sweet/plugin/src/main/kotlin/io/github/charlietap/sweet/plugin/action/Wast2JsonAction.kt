package io.github.charlietap.sweet.plugin.action

import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

interface Wast2JsonParams: WorkParameters {
    val wast2JsonFile: RegularFileProperty
    val inputFile: RegularFileProperty
    val outputDirectory: DirectoryProperty
}

abstract class Wast2JsonAction : WorkAction<Wast2JsonParams> {

    @get:Inject
    abstract val cli: ExecOperations

    override fun execute() {

        val outputDir = parameters.outputDirectory.get().asFile
        if(!outputDir.exists()) {
            outputDir.mkdirs()
        }

        cli.exec {
            workingDir = outputDir
            executable = parameters.wast2JsonFile.get().asFile.absolutePath
            args = listOf(
                parameters.inputFile.get().asFile.absolutePath,
            ) + ADDITIONAL_FLAGS
        }
    }

    private companion object {
        val ADDITIONAL_FLAGS = setOf(
            "--enable-tail-call",
            "--enable-extended-const",
        )
    }
}
