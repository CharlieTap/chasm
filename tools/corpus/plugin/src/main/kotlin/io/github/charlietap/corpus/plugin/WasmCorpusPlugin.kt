package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.plugin.task.CorpusMatrixTask
import io.github.charlietap.corpus.plugin.task.GenerateCorpusReportTask
import io.github.charlietap.corpus.plugin.task.GenerateCorpusTestsTask
import io.github.charlietap.corpus.plugin.task.PrepareCorpusResourcesTask
import io.github.charlietap.corpus.plugin.task.ResolveCorpusFixturesTask
import io.github.charlietap.corpus.plugin.task.SyncCorpusRepositoryTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.TEST_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests.Companion.DEFAULT_TEST_RUN_NAME
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

class WasmCorpusPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<WasmCorpusPluginExtension>("corpus")
        val effectivePhase = project.providers.gradleProperty(PROP_PHASE)
            .map(::parsePhase)
            .orElse(extension.phase)
        val machineMetadata = project.providers.of(CorpusMachineValueSource::class.java) {}
        val projectDirectory = project.layout.projectDirectory.asFile.toPath()
        extension.targets.convention(
            project.providers.gradleProperty(PROP_TARGETS).map(::parseTargets).orElse(emptyList()),
        )
        val corpusRequested = project.isCorpusRequested()
        val baselineRequested = project.isTaskRequested(TASK_NAME_UPDATE_BASELINE)

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
            languages.set(extension.languages)
            requiredFeatures.set(extension.requiredFeatures)
            excludedFeatures.set(extension.excludedFeatures)
            tags.set(extension.tags)
            excludedTags.set(extension.excludedTags)
            size.set(extension.size)
            duration.set(extension.duration)
            outputFile.set(extension.corpusFixtureDirectory.file("fixtures.json"))
        }

        val generateTests = project.tasks.register<GenerateCorpusTestsTask>(TASK_NAME_GENERATE_TESTS) {
            group = GROUP
            description = "Generates Kotlin tests from wasm-corpus fixtures"

            corpusRunner.set(extension.corpusRunner)
            testPackageName.set(extension.testPackageName)
            phase.set(effectivePhase)
            resultsDirectoryPath.set(
                project.providers.provider {
                    projectDirectory.relativize(extension.corpusResultsDirectory.get().asFile.toPath()).toString()
                },
            )
            outputDirectory.set(extension.corpusTestsDirectory)
        }

        val prepareResources = project.tasks.register<PrepareCorpusResourcesTask>(TASK_NAME_PREPARE_RESOURCES) {
            group = GROUP
            description = "Packages selected wasm-corpus fixtures as test resources"

            corpusDirectory.set(syncCorpus.flatMap { it.outputDirectory })
            fixturesIndex.set(resolveFixtures.flatMap { it.outputFile })
            targets.set(extension.targets)
            excludedTargets.set(extension.excludedTargets)
            outputDirectory.set(extension.corpusResourcesDirectory)
        }

        project.tasks.register<CorpusMatrixTask>(TASK_NAME_MATRIX) {
            group = GROUP
            description = "Prints a wasm-corpus fixture matrix"

            fixturesIndex.set(resolveFixtures.flatMap { it.outputFile })
        }

        val generateCorpusReport = project.tasks.register<GenerateCorpusReportTask>(TASK_NAME_GENERATE_REPORT) {
            group = GROUP
            description = "Aggregates wasm-corpus fixture timings into a JSON report"

            machine.set(machineMetadata)
            repositoryUrl.set(extension.corpusRepositoryUrl)
            revision.set(extension.corpusRef)
            phase.set(effectivePhase.map { value -> value.name.lowercase() })
            versions.set(extension.versions)
            languages.set(extension.languages)
            requiredFeatures.set(extension.requiredFeatures)
            excludedFeatures.set(extension.excludedFeatures)
            tags.set(extension.tags)
            excludedTags.set(extension.excludedTags)
            size.set(extension.size)
            duration.set(extension.duration)
            targets.set(extension.targets)
            excludedTargets.set(extension.excludedTargets)
            gradleVersion.set(GradleVersion.current().version)
            gradleJavaLanguageVersion.set(project.providers.systemProperty("java.specification.version"))
            gradleJavaRuntimeVersion.set(project.providers.systemProperty("java.runtime.version"))
            gradleJvmVersion.set(project.providers.systemProperty("java.vm.version"))
            gradleJavaVendor.set(project.providers.systemProperty("java.vendor"))
            outputFile.set(extension.corpusReportFile)
        }

        project.tasks.register<Delete>(TASK_NAME_CLEAN_TESTS) {
            group = GROUP
            description = "Removes generated wasm-corpus tests and reports"

            delete(extension.corpusFixtureDirectory)
            delete(extension.corpusTestsDirectory)
            delete(extension.corpusResourcesDirectory)
            delete(extension.corpusResultsDirectory)
            delete(extension.corpusReportFile)
        }

        val kotlinExtension = project.extensions.getByType<KotlinMultiplatformExtension>()
        val jvmTarget = kotlinExtension.targets.withType(KotlinJvmTarget::class.java).single()
        val jvmTestCompilation = jvmTarget.compilations.getByName(TEST_COMPILATION_NAME)
        val jvmTest = jvmTarget.testRuns.getByName(DEFAULT_TEST_RUN_NAME).executionTask
        val testJavaLauncher = jvmTest.flatMap { test -> test.javaLauncher }
        generateCorpusReport.configure {
            resultFiles.from(
                jvmTest.flatMap { test -> test.binaryResultsDirectory }.map { directory ->
                    directory.asFileTree.matching {
                        include("**/*.json")
                    }
                },
            )
            testJavaLanguageVersion.set(
                testJavaLauncher.map { launcher -> launcher.metadata.languageVersion.toString() },
            )
            testJavaRuntimeVersion.set(
                testJavaLauncher.map { launcher -> launcher.metadata.javaRuntimeVersion },
            )
            testJvmVersion.set(
                testJavaLauncher.map { launcher -> launcher.metadata.jvmVersion },
            )
            testJavaVendor.set(
                testJavaLauncher.map { launcher -> launcher.metadata.vendor },
            )
        }

        jvmTestCompilation.defaultSourceSet.apply {
            val corpusTestsDirectory = if (corpusRequested) {
                generateTests.flatMap { it.outputDirectory }
            } else {
                extension.corpusTestsDirectory
            }
            kotlin.srcDir(corpusTestsDirectory)
            val corpusResourcesDirectory = if (corpusRequested) {
                prepareResources.flatMap { it.outputDirectory }
            } else {
                extension.corpusResourcesDirectory
            }
            resources.srcDir(corpusResourcesDirectory)
        }

        val corpus = project.tasks.register(TASK_NAME_CORPUS) {
            group = GROUP
            description = "Runs wasm-corpus fixtures against the JVM test runtime"

            dependsOn(generateTests)
            dependsOn(prepareResources)
            dependsOn(jvmTest)
            dependsOn(generateCorpusReport)
        }

        project.tasks.register<Copy>(TASK_NAME_UPDATE_BASELINE) {
            group = GROUP
            description = "Runs wasm-corpus and updates the checked-in results baseline"

            dependsOn(corpus)
            from(generateCorpusReport.flatMap { task -> task.outputFile })
            into(extension.corpusBaselineDirectory)
            rename { BASELINE_FILE_NAME }
        }

        jvmTest.configure {
            maxHeapSize = "2g"
            jvmArgs("-Xss32m")

            if (corpusRequested) {
                workingDirectory.set(project.layout.projectDirectory)
                binaryResultsDirectory.set(extension.corpusResultsDirectory)
                inputs.property("corpusMachine", machineMetadata)
                include("**/corpus/generated/**")

                if (baselineRequested) {
                    outputs.upToDateWhen { false }
                    outputs.doNotCacheIf("Baseline updates require a fresh corpus measurement") { true }
                }
            } else {
                exclude("**/corpus/generated/**")
            }
        }
    }

    private fun Project.isCorpusRequested(): Boolean = gradle.startParameter.taskNames.any { taskName ->
        taskName == TASK_NAME_CORPUS ||
            taskName == TASK_NAME_LEGACY_CORPUS ||
            taskName == TASK_NAME_GENERATE_REPORT ||
            taskName == TASK_NAME_UPDATE_BASELINE ||
            taskName.endsWith(":$TASK_NAME_CORPUS") ||
            taskName.endsWith(":$TASK_NAME_LEGACY_CORPUS") ||
            taskName.endsWith(":$TASK_NAME_GENERATE_REPORT") ||
            taskName.endsWith(":$TASK_NAME_UPDATE_BASELINE")
    }

    private fun Project.isTaskRequested(name: String): Boolean = gradle.startParameter.taskNames.any { taskName ->
        taskName == name || taskName.endsWith(":$name")
    }

    private fun parseTargets(value: String): List<String> = value
        .split(",")
        .map(String::trim)
        .filter(String::isNotEmpty)

    private fun parsePhase(value: String): CorpusPhase {
        val phase = CorpusPhase.entries.firstOrNull { phase ->
            phase.name.equals(value.trim(), ignoreCase = true)
        }
        return requireNotNull(phase) {
            "Invalid $PROP_PHASE value '$value'. Expected one of: ${
                CorpusPhase.entries.joinToString { phase -> phase.name.lowercase() }
            }"
        }
    }

    private companion object {
        const val PROP_PHASE = "wasmCorpus.phase"
        const val PROP_TARGETS = "wasmCorpus.targets"
        const val GROUP = "corpus"
        const val TASK_NAME_CORPUS = "corpus"
        const val TASK_NAME_LEGACY_CORPUS = "wasmCorpusTest"
        const val TASK_NAME_SYNC_CORPUS = "syncWasmCorpus"
        const val TASK_NAME_RESOLVE_FIXTURES = "resolveCorpusFixtures"
        const val TASK_NAME_GENERATE_TESTS = "generateCorpusTests"
        const val TASK_NAME_PREPARE_RESOURCES = "prepareCorpusResources"
        const val TASK_NAME_GENERATE_REPORT = "generateCorpusReport"
        const val TASK_NAME_UPDATE_BASELINE = "updateCorpusBaseline"
        const val TASK_NAME_MATRIX = "corpusMatrix"
        const val TASK_NAME_CLEAN_TESTS = "cleanCorpusTests"
        const val BASELINE_FILE_NAME = "baseline.json"
    }
}
