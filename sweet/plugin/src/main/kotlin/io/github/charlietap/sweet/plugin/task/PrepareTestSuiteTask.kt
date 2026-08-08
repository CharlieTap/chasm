package io.github.charlietap.sweet.plugin.task

import io.github.charlietap.sweet.plugin.action.WasmToolsAction
import io.github.charlietap.sweet.plugin.ext.relativeSuitePath
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
abstract class PrepareTestSuiteTask : DefaultTask() {

    @get:Incremental
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ConfigurableFileCollection

    @get:Internal
    abstract val sourceDirectory: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val wast2Json: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun prepare(inputChanges: InputChanges) {
        if (!inputChanges.isIncremental) {
            outputDirectory.get().asFile.deleteRecursively()
        }

        val sourceRoot = sourceDirectory.get().asFile
        inputChanges.getFileChanges(inputFiles).forEach { change ->
            if (change.file.isDirectory) return@forEach

            val sourceRelativePath = change.file.relativeSuitePath(sourceRoot)
            val generatedDirectory = outputDirectory.dir(sourceRelativePath.removeSuffix(WAST_EXTENSION))

            when (change.changeType) {
                ChangeType.REMOVED -> generatedDirectory.get().asFile.deleteRecursively()
                ChangeType.MODIFIED -> {
                    generatedDirectory.get().asFile.deleteRecursively()
                    queueWasmTools(change, generatedDirectory)
                }
                ChangeType.ADDED -> queueWasmTools(change, generatedDirectory)
            }
        }
    }

    private fun queueWasmTools(
        change: FileChange,
        generatedDirectory: Provider<Directory>,
    ) {
        workerExecutor.noIsolation().submit(WasmToolsAction::class.java) {
            inputFile.set(change.file)
            outputDirectory.set(generatedDirectory)
            wasmToolsFile.set(wast2Json)
        }
    }

    private companion object {
        const val WAST_EXTENSION = ".wast"
    }
}
