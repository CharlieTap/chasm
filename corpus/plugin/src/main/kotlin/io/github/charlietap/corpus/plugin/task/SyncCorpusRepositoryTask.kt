package io.github.charlietap.corpus.plugin.task

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
abstract class SyncCorpusRepositoryTask : DefaultTask() {

    @get:Input
    abstract val repositoryUrl: Property<String>

    @get:Input
    abstract val ref: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val cli: ExecOperations

    @TaskAction
    fun sync() {
        try {
            syncRepository()
        } catch (_: Exception) {
            wipeDirectory()
            syncRepository()
        }
    }

    private fun syncRepository() {
        outputDirectory.get().asFile.parentFile.mkdirs()
        if (repositoryHasBeenCloned()) {
            fetchRepo()
        } else {
            cloneRepo()
        }
        checkoutRef()
    }

    private fun cloneRepo() {
        cli.exec {
            workingDir = outputDirectory.get().asFile.parentFile
            commandLine("git", "clone", repositoryUrl.get(), outputDirectory.get().asFile.absolutePath)
        }
    }

    private fun fetchRepo() {
        cli.exec {
            workingDir = outputDirectory.get().asFile
            commandLine("git", "fetch", "--all", "--tags")
        }
    }

    private fun checkoutRef() {
        cli.exec {
            workingDir = outputDirectory.get().asFile
            commandLine("git", "checkout", ref.get())
        }
    }

    private fun repositoryHasBeenCloned(): Boolean {
        val outputDir = outputDirectory.get().asFile
        return outputDir.exists() && outputDir.isDirectory && Files.list(outputDir.toPath()).use { files ->
            files.findAny().isPresent
        }
    }

    private fun wipeDirectory() {
        outputDirectory.get().asFile.deleteRecursively()
    }
}
