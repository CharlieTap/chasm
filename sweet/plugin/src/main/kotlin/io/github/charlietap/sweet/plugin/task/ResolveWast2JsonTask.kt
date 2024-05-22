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
abstract class ResolveWast2JsonTask : DefaultTask() {

    @get:Input
    abstract val wabtVersion: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val wabtDirectory: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Inject
    abstract val cli: ExecOperations

    @TaskAction
    fun resolve() {

        val wast2JsonFile = outputFile.get().asFile

        if(!wast2JsonFile.exists()) {
            throw GradleException("wast2json file does not exist: ${wast2JsonFile.absolutePath}")
        }

        val versionBytes = ByteArrayOutputStream()

        cli.exec {
            commandLine = listOf(outputFile.get().asFile.absolutePath, "--version")
            standardOutput = versionBytes
        }

        val version = versionBytes.toString().trim()

        if(version != wabtVersion.get()) {
            throw GradleException("Wast2JsonTask does not support version $wabtVersion")
        }
    }
}
