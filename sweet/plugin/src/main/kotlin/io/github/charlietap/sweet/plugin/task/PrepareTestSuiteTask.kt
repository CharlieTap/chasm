package io.github.charlietap.sweet.plugin.task

import io.github.charlietap.sweet.plugin.action.Wast2JsonAction
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor

@CacheableTask
abstract class PrepareTestSuiteTask : DefaultTask() {

    @get:Incremental
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ConfigurableFileTree

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val wast2Json: RegularFileProperty

    @get:Input
    abstract val excludes: ListProperty<String>

    @get:Input
    abstract val proposals: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun prepare(inputChanges: InputChanges) {

        val queue = workerExecutor.noIsolation()

        inputChanges.getFileChanges(inputFiles).forEach { change ->

            if(change.file.isDirectory) return@forEach

            val generatedDirectory = if(change.file.path.contains("proposals")) {
                val dir = "proposal/${change.file.parentFile.name}/${change.file.nameWithoutExtension}"
                outputDirectory.dir(dir)
            } else {
                outputDirectory.dir(change.file.nameWithoutExtension)
            }

            queue.submit(Wast2JsonAction::class.java) {
                inputFile.set(change.file)
                outputDirectory.set(generatedDirectory)
                wast2JsonFile.set(wast2Json)
            }
        }
    }
}
