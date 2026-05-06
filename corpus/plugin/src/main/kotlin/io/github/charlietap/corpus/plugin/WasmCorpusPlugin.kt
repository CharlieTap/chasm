package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.plugin.task.CorpusMatrixTask
import io.github.charlietap.corpus.plugin.task.CleanCorpusTestsTask
import io.github.charlietap.corpus.plugin.task.GenerateCorpusTestsTask
import io.github.charlietap.corpus.plugin.task.ResolveCorpusFixturesTask
import io.github.charlietap.corpus.plugin.task.SyncCorpusRepositoryTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class WasmCorpusPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<WasmCorpusPluginExtension>("corpus")

        val syncCorpus = project.tasks.register<SyncCorpusRepositoryTask>(TASK_NAME_SYNC_CORPUS) {
            group = GROUP
            description = "Clones or updates the wasm-corpus repository"

            repositoryUrl.set(extension.corpusRepositoryUrl)
            ref.set(extension.corpusRef)
            outputDirectory.set(extension.corpusDirectory)
        }

        val resolveFixtures = project.tasks.register<ResolveCorpusFixturesTask>(TASK_NAME_RESOLVE_FIXTURES) {
            group = GROUP
            description = "Resolves wasm-corpus fixture metadata"

            corpusDirectory.set(syncCorpus.flatMap { it.outputDirectory })
            versions.set(extension.versions)
            requiredFeatures.set(extension.requiredFeatures)
            excludedFeatures.set(extension.excludedFeatures)
            tags.set(extension.tags)
            outputFile.set(extension.corpusFixtureDirectory.file("fixtures.json"))
        }

        val generateTests = project.tasks.register<GenerateCorpusTestsTask>(TASK_NAME_GENERATE_TESTS) {
            group = GROUP
            description = "Generates Kotlin tests from wasm-corpus fixtures"

            fixturesIndex.set(resolveFixtures.flatMap { it.outputFile })
            corpusDirectory.set(syncCorpus.flatMap { it.outputDirectory })
            corpusRunner.set(extension.corpusRunner)
            testPackageName.set(extension.testPackageName)
            phase.set(extension.phase)
            targets.set(extension.targets)
            excludedTargets.set(extension.excludedTargets)
            outputDirectory.set(extension.corpusTestsDirectory)
        }

        project.tasks.register<CorpusMatrixTask>(TASK_NAME_MATRIX) {
            group = GROUP
            description = "Prints a wasm-corpus fixture matrix"

            fixturesIndex.set(resolveFixtures.flatMap { it.outputFile })
        }

        project.tasks.register<CleanCorpusTestsTask>(TASK_NAME_CLEAN_TESTS) {
            group = GROUP
            description = "Removes generated wasm-corpus fixtures and Kotlin tests"

            corpusFixtureDirectory.set(extension.corpusFixtureDirectory)
            corpusTestsDirectory.set(extension.corpusTestsDirectory)
        }

        project.tasks.named { it.contains("compileTestKotlin") }.configureEach {
            dependsOn(generateTests)
        }

        project.tasks.named { it.startsWith("formatKotlin") }.configureEach {
            dependsOn(generateTests)
        }

        val kotlinExtension = project.extensions.getByType<KotlinMultiplatformExtension>()
        kotlinExtension.sourceSets.getByName("commonTest") {
            kotlin.srcDir(extension.corpusTestsDirectory)
        }
    }

    private companion object {
        const val GROUP = "corpus"
        const val TASK_NAME_SYNC_CORPUS = "syncWasmCorpus"
        const val TASK_NAME_RESOLVE_FIXTURES = "resolveCorpusFixtures"
        const val TASK_NAME_GENERATE_TESTS = "generateCorpusTests"
        const val TASK_NAME_MATRIX = "corpusMatrix"
        const val TASK_NAME_CLEAN_TESTS = "cleanCorpusTests"
    }
}
