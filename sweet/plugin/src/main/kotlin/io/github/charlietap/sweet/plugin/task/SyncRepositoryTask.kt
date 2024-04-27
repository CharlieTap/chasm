package io.github.charlietap.sweet.plugin.task

import java.nio.file.Files
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

@CacheableTask
abstract class SyncRepositoryTask : DefaultTask() {

    @get:Input
    abstract val repositoryUrl: Property<String>

    @get:Input
    abstract val commitHash: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val cli: ExecOperations

    @TaskAction
    fun sync() {
        if (repositoryHasBeenCloned()) {
             cli.exec {
                workingDir = outputDirectory.get().asFile
                commandLine("git", "fetch", "--all")
             }
        } else {
            cli.exec {
                workingDir = outputDirectory.get().asFile
                commandLine("git", "clone", repositoryUrl.get(), outputDirectory.get().asFile.absolutePath)
            }
        }

        cli.exec {
            workingDir = outputDirectory.get().asFile
            commandLine("git", "checkout", commitHash.get())
        }
    }

    private fun repositoryHasBeenCloned(): Boolean {

        val outputDir = outputDirectory.get().asFile

        return if(outputDir.exists() && outputDir.isDirectory) {
            val path = outputDir.toPath()

            Files.list(path).findAny().isPresent
        } else false
    }
}
