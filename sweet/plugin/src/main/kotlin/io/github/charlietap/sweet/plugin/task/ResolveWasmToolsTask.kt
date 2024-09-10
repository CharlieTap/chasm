package io.github.charlietap.sweet.plugin.task

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

@CacheableTask
abstract class ResolveWasmToolsTask : DefaultTask() {

    @get:Input
    abstract val wasmToolsVersion: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val wasmToolsDirectory: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Inject
    abstract val cli: ExecOperations

    @TaskAction
    fun resolve() {

        val wasmToolsFile = outputFile.get().asFile

        if(!wasmToolsFile.exists()) {
            throw GradleException("wasm-tools file does not exist: ${wasmToolsFile.absolutePath}")
        }

        val versionBytes = ByteArrayOutputStream()

        cli.exec {
            commandLine = listOf(outputFile.get().asFile.absolutePath, "--version")
            standardOutput = versionBytes
        }

        val version = versionBytes.toString().trim()

        if(!version.contains(wasmToolsVersion.get())) {
            throw GradleException("wasm-tools does not support version $wasmToolsVersion")
        }
    }
}
