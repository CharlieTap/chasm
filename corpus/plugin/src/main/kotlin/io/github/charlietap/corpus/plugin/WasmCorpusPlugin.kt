package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.plugin.task.CorpusMatrixTask
import io.github.charlietap.corpus.plugin.task.CleanCorpusTestsTask
import io.github.charlietap.corpus.plugin.task.GenerateCorpusTestsTask
import io.github.charlietap.corpus.plugin.task.ResolveCorpusFixturesTask
import io.github.charlietap.corpus.plugin.task.SyncCorpusRepositoryTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class WasmCorpusPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<WasmCorpusPluginExtension>("corpus")
        val corpusRequested = project.isCorpusRequested()

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

        project.tasks.register(TASK_NAME_CORPUS) {
            group = GROUP
            description = "Runs wasm-corpus fixtures against the JVM test runtime"

            dependsOn(generateTests)
            dependsOn(project.tasks.named(TASK_NAME_JVM_TEST))
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

        val kotlinExtension = project.extensions.getByType<KotlinMultiplatformExtension>()
        kotlinExtension.sourceSets.getByName("jvmTest") {
            kotlin.srcDir(extension.corpusTestsDirectory)
        }

        if (corpusRequested) {
            project.tasks.named(TASK_NAME_COMPILE_JVM_TEST).configure {
                dependsOn(generateTests)
            }
        }

        project.tasks.named<Test>(TASK_NAME_JVM_TEST).configure {
            maxHeapSize = "2g"
            jvmArgs("-Xss32m")

            if (corpusRequested) {
                include("**/corpus/generated/**")
            } else {
                exclude("**/corpus/generated/**")
            }
        }
    }

    private fun Project.isCorpusRequested(): Boolean = gradle.startParameter.taskNames.any { taskName ->
        taskName == TASK_NAME_CORPUS ||
            taskName == TASK_NAME_LEGACY_CORPUS ||
            taskName.endsWith(":$TASK_NAME_CORPUS") ||
            taskName.endsWith(":$TASK_NAME_LEGACY_CORPUS")
    }

    private companion object {
        const val GROUP = "corpus"
        const val TASK_NAME_CORPUS = "corpus"
        const val TASK_NAME_LEGACY_CORPUS = "wasmCorpusTest"
        const val TASK_NAME_SYNC_CORPUS = "syncWasmCorpus"
        const val TASK_NAME_RESOLVE_FIXTURES = "resolveCorpusFixtures"
        const val TASK_NAME_GENERATE_TESTS = "generateCorpusTests"
        const val TASK_NAME_MATRIX = "corpusMatrix"
        const val TASK_NAME_CLEAN_TESTS = "cleanCorpusTests"
        const val TASK_NAME_JVM_TEST = "jvmTest"
        const val TASK_NAME_COMPILE_JVM_TEST = "compileTestKotlinJvm"
    }
}
