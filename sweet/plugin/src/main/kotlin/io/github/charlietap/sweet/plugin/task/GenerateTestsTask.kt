package io.github.charlietap.sweet.plugin.task

import io.github.charlietap.sweet.plugin.action.GenerateTestAction
import io.github.charlietap.sweet.plugin.ext.snakeCaseToPascalCase
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor

@CacheableTask
abstract class GenerateTestsTask : DefaultTask() {

    @get:Incremental
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ConfigurableFileTree

    @get:Input
    abstract val scriptRunner: Property<String>

    @get:Input
    abstract val testPackageName: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun generate(inputChanges: InputChanges) {

        val queue = workerExecutor.noIsolation()

        inputChanges.getFileChanges(inputFiles).forEach { change ->

            if(change.file.isDirectory) return@forEach

            val testPackageDirPath = testPackageName.get().replace(".", "/")
            val testPackageDir = outputDirectory.dir(testPackageDirPath).get()
            val name = change.file.nameWithoutExtension.snakeCaseToPascalCase() + ".kt"

            val test = if(change.file.path.contains("proposal")) {
                val dir = "proposal/${change.file.parentFile.name}"
                testPackageDir.dir(dir).file(name)
            } else {
                testPackageDir.dir(change.file.nameWithoutExtension).file(name)
            }

            queue.submit(GenerateTestAction::class.java) {
                runner = scriptRunner
                scriptFile = change.file
                testPackage = testPackageName
                testFile = test
            }
        }
    }
}
