package io.github.charlietap.sweet.plugin.task

import io.github.charlietap.sweet.plugin.Proposal
import io.github.charlietap.sweet.plugin.action.GenerateTestAction
import io.github.charlietap.sweet.plugin.ext.backtrackCollectingDirectoriesUntil
import io.github.charlietap.sweet.plugin.ext.snakeCaseToPascalCase
import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
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
    abstract val excludes: ListProperty<String>

    @get:Input
    abstract val proposals: ListProperty<Proposal>

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

        inputChanges.getFileChanges(inputFiles).forEach { change ->

            if(change.file.isDirectory) return@forEach

            val testPackageDirPath = testPackageName.get().replace(".", File.separator)
            val testPackageDir = outputDirectory.dir(testPackageDirPath).get()
            val name = change.file.nameWithoutExtension.snakeCaseToPascalCase() + "Test.kt"

            val test = if(change.file.path.contains(DIRECTORY_PROPOSAL)) {

                val directories = change.file.backtrackCollectingDirectoriesUntil { file ->
                    file.parentFile.name == DIRECTORY_PROPOSAL
                }
                var outputDirectory = testPackageDir.dir(DIRECTORY_PROPOSAL)
                directories.asReversed().forEach { directory ->
                    outputDirectory = outputDirectory.dir(directory)
                }

                outputDirectory.file(name)
            } else {
                testPackageDir.dir(change.file.nameWithoutExtension).file(name)
            }

            when(change.changeType) {
                ChangeType.REMOVED -> {
                    test.asFile.parentFile.deleteRecursively()
                }
                ChangeType.MODIFIED -> {
                    test.asFile.delete()
                    queueJobToGenerateTest(change, test)
                }
                ChangeType.ADDED -> {
                    queueJobToGenerateTest(change, test)
                }
            }
        }
    }

    private fun queueJobToGenerateTest(
        change: FileChange,
        generatedTestFile: RegularFile,
    ) {
        val queue = workerExecutor.noIsolation()
        queue.submit(GenerateTestAction::class.java) {
            proposal = proposals
            runner = scriptRunner
            scriptFile = change.file
            testPackage = testPackageName
            testFile = generatedTestFile
        }
    }

    private companion object {
        const val DIRECTORY_PROPOSAL = "proposal"
    }
}
