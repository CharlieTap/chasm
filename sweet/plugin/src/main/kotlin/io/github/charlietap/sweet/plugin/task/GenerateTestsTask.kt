package io.github.charlietap.sweet.plugin.task

import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.PhaseLimit
import io.github.charlietap.sweet.plugin.action.GenerateTestAction
import io.github.charlietap.sweet.plugin.ext.deleteAndPruneEmptyParents
import io.github.charlietap.sweet.plugin.ext.generatedTestLocation
import io.github.charlietap.sweet.plugin.ext.relativeSuitePath
import io.github.charlietap.sweet.plugin.ext.resolvePhaseSupport
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
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
abstract class GenerateTestsTask : DefaultTask() {

    @get:Incremental
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ConfigurableFileCollection

    @get:Internal
    abstract val intermediateDirectory: DirectoryProperty

    @get:Input
    abstract val sourceName: Property<String>

    @get:Input
    abstract val phaseSupport: Property<SemanticPhase>

    @get:Input
    abstract val phaseLimits: ListProperty<PhaseLimit>

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
        val outputRoot = outputDirectory.get().asFile
        if (!inputChanges.isIncremental) {
            outputRoot.deleteRecursively()
        }

        val intermediateRoot = intermediateDirectory.get().asFile
        inputChanges.getFileChanges(inputFiles).forEach { change ->
            if (change.file.isDirectory) return@forEach

            val scriptRelativePath = change.file.relativeSuitePath(intermediateRoot)
            val location = generatedTestLocation(
                sourceName = sourceName.get(),
                testPackageName = testPackageName.get(),
                scriptRelativePath = scriptRelativePath,
            )
            val generatedTestFile = outputDirectory.file(location.outputRelativePath).get()

            when (change.changeType) {
                ChangeType.REMOVED -> generatedTestFile.asFile.deleteAndPruneEmptyParents(outputRoot)
                ChangeType.MODIFIED -> {
                    generatedTestFile.asFile.delete()
                    queueTestGeneration(change, generatedTestFile, location.packageName, location.sourceRelativeWastPath)
                }
                ChangeType.ADDED -> {
                    queueTestGeneration(change, generatedTestFile, location.packageName, location.sourceRelativeWastPath)
                }
            }
        }
    }

    private fun queueTestGeneration(
        change: FileChange,
        generatedTestFile: RegularFile,
        testPackage: String,
        sourceRelativeWastPath: String,
    ) {
        workerExecutor.noIsolation().submit(GenerateTestAction::class.java) {
            phaseSupport.set(
                resolvePhaseSupport(
                    sourceRelativePath = sourceRelativeWastPath,
                    defaultPhaseSupport = this@GenerateTestsTask.phaseSupport.get(),
                    phaseLimits = this@GenerateTestsTask.phaseLimits.get(),
                ),
            )
            runner.set(scriptRunner)
            scriptFile.set(change.file)
            this.testPackage.set(testPackage)
            testFile.set(generatedTestFile)
        }
    }
}
