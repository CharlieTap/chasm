package io.github.charlietap.sweet.plugin.task

import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

@CacheableTask
abstract class ResolveWast2JsonTask : DefaultTask() {

    @get:Input
    abstract val wabtVersion: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Inject
    abstract val cli: ExecOperations

    @get:Inject
    abstract val fs: FileSystemOperations

    @TaskAction
    fun resolve() {
        val existingLocationBytes = ByteArrayOutputStream()

        cli.exec {
            commandLine = listOf("which", "wast2json")
            standardOutput = existingLocationBytes
        }

        val existingLocation =  File(existingLocationBytes.toString().trim())

        if(!outputFile.get().asFile.exists()) {
            fs.copy {
                from(existingLocation)
                into(outputFile.get().asFile.parentFile)
            }
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
