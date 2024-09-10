package io.github.charlietap.sweet.plugin.action

import java.io.File
import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

interface WasmToolsParams: WorkParameters {
    val wasmToolsFile: RegularFileProperty
    val inputFile: RegularFileProperty
    val outputDirectory: DirectoryProperty
}

abstract class WasmToolsAction : WorkAction<WasmToolsParams> {

    @get:Inject
    abstract val cli: ExecOperations

    override fun execute() {

        val outputDir = parameters.outputDirectory.get().asFile
        if(!outputDir.exists()) {
            outputDir.mkdirs()
        }

        cli.exec {
            workingDir = outputDir
            executable = parameters.wasmToolsFile.get().asFile.absolutePath
            args = listOf(
                CLI_ARG_J2W,
                CLI_OPTION_DIR, outputDir.absolutePath,
                CLI_OPTION_OUTPUT, outputDir.absolutePath + File.separator + outputDir.nameWithoutExtension + ".json",
                parameters.inputFile.get().asFile.absolutePath,
            )
        }
    }

    private companion object {
        const val CLI_ARG_J2W = "json-from-wast"

        const val CLI_OPTION_DIR = "--wasm-dir"
        const val CLI_OPTION_OUTPUT = "-o"
    }
}
