package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.plugin.task.CorpusMatrixTask
import io.github.charlietap.corpus.plugin.task.GenerateCorpusTestsTask
import io.github.charlietap.corpus.plugin.task.PrepareCorpusResourcesTask
import io.github.charlietap.corpus.plugin.task.ResolveCorpusFixturesTask
import io.github.charlietap.corpus.plugin.task.SyncCorpusRepositoryTask
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

data class RegisteredFixtures(
    val prepareResources: TaskProvider<PrepareCorpusResourcesTask>,
    val generateTests: TaskProvider<GenerateCorpusTestsTask>,
)

class FixtureTasks(
    private val project: Project,
    private val extension: WasmCorpusPluginExtension,
    private val runProperties: RunProperties,
) {

    fun register(): RegisteredFixtures {

        val sync = project.tasks.register<SyncCorpusRepositoryTask>(TASK_SYNC) {
            group = GROUP
            description = "Clones or updates the wasm-corpus repository"

            repositoryUrl.set(extension.corpusRepositoryUrl)
            ref.set(extension.corpusRef)
            outputDirectory.set(extension.corpusDirectory)
        }

        val resolve = project.tasks.register<ResolveCorpusFixturesTask>(TASK_RESOLVE) {
            group = GROUP
            description = "Resolves wasm-corpus fixture metadata"

            corpusDirectory.set(sync.flatMap { task -> task.outputDirectory })
            versions.set(extension.versions)
            languages.set(extension.languages)
            requiredFeatures.set(extension.requiredFeatures)
            excludedFeatures.set(extension.excludedFeatures)
            tags.set(extension.tags)
            excludedTags.set(extension.excludedTags)
            size.set(extension.size)
            duration.set(extension.duration)
            outputFile.set(extension.corpusFixtureDirectory.file(FIXTURES_FILE))
        }

        val prepare = project.tasks.register<PrepareCorpusResourcesTask>(TASK_PREPARE) {
            group = GROUP
            description = "Packages the selected wasm-corpus fixtures and the run they were selected for as test resources"

            corpusDirectory.set(sync.flatMap { task -> task.outputDirectory })
            resolvedFixtures.set(resolve.flatMap { task -> task.outputFile })
            repositoryUrl.set(extension.corpusRepositoryUrl)
            revision.set(extension.corpusRef)
            phase.set(runProperties.phase)
            engines.set(runProperties.engines)
            warmups.set(runProperties.warmups)
            rounds.set(runProperties.rounds)
            pin.set(runProperties.pin)
            versions.set(extension.versions)
            languages.set(extension.languages)
            requiredFeatures.set(extension.requiredFeatures)
            excludedFeatures.set(extension.excludedFeatures)
            tags.set(extension.tags)
            excludedTags.set(extension.excludedTags)
            size.set(extension.size)
            duration.set(extension.duration)
            targets.set(runProperties.targets)
            excludedTargets.set(extension.excludedTargets)
            outputDirectory.set(extension.corpusResourcesDirectory)
        }

        val generate = project.tasks.register<GenerateCorpusTestsTask>(TASK_GENERATE) {
            group = GROUP
            description = "Generates one Kotlin test per selected wasm-corpus fixture"

            fixturesIndex.set(prepare.flatMap { task -> task.fixturesIndex })
            corpusRunner.set(extension.corpusRunner)
            testPackageName.set(extension.testPackageName)
            phase.set(runProperties.phase)
            engines.set(runProperties.engines)
            warmups.set(runProperties.warmups)
            rounds.set(runProperties.rounds)
            pin.set(runProperties.pin)
            outputDirectory.set(extension.corpusTestsDirectory)
        }

        project.tasks.register<CorpusMatrixTask>(TASK_MATRIX) {
            group = GROUP
            description = "Prints a wasm-corpus fixture matrix"

            fixturesIndex.set(resolve.flatMap { task -> task.outputFile })
        }

        project.tasks.register<Delete>(TASK_CLEAN) {
            group = GROUP
            description = "Removes generated wasm-corpus tests, measurements and reports"

            delete(extension.corpusFixtureDirectory)
            delete(extension.corpusTestsDirectory)
            delete(extension.corpusResourcesDirectory)
            delete(extension.corpusMeasurementsDirectory)
            delete(extension.corpusReportsDirectory)
        }

        return RegisteredFixtures(prepare, generate)
    }

    private companion object {
        const val TASK_SYNC = "syncWasmCorpus"
        const val TASK_RESOLVE = "resolveCorpusFixtures"
        const val TASK_PREPARE = "prepareCorpusResources"
        const val TASK_GENERATE = "generateCorpusTests"
        const val TASK_MATRIX = "corpusMatrix"
        const val TASK_CLEAN = "cleanCorpusTests"
        const val FIXTURES_FILE = "fixtures.json"
    }
}
