package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.plugin.task.CorpusMatrixTask
import io.github.charlietap.corpus.plugin.task.CorpusMeasurementTest
import io.github.charlietap.corpus.plugin.task.GenerateCorpusReportTask
import io.github.charlietap.corpus.plugin.task.GenerateCorpusTestsTask
import io.github.charlietap.corpus.plugin.task.PrepareCorpusResourcesTask
import io.github.charlietap.corpus.plugin.task.ResolveCorpusFixturesTask
import io.github.charlietap.corpus.plugin.task.SyncCorpusRepositoryTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
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
        extension.targets.convention(
            project.providers.gradleProperty(PROP_TARGETS).map(::parseTargets).orElse(emptyList()),
        )

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

        val measurementResultsDirectory = project.layout.buildDirectory.dir(DIR_MEASUREMENT_RESULTS)
        val measurementReportFile = project.layout.buildDirectory.file(FILE_MEASUREMENT_REPORT)

        project.tasks.register<Delete>(TASK_NAME_CLEAN_TESTS) {
            group = GROUP
            description = "Removes generated wasm-corpus tests and reports"

            delete(extension.corpusFixtureDirectory)
            delete(extension.corpusTestsDirectory)
            delete(extension.corpusResourcesDirectory)
            delete(extension.corpusResultsDirectory)
            delete(extension.corpusReportFile)
            delete(measurementResultsDirectory)
            delete(measurementReportFile)
        }

        project.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
            var jvmTargetConfigured = false
            project.extensions.getByType<KotlinMultiplatformExtension>().targets.withType<KotlinJvmTarget>().configureEach {
                check(!jvmTargetConfigured) { "wasm-corpus supports a single JVM target, found another: $name" }
                jvmTargetConfigured = true

                val jvmTestCompilation = compilations.getByName(TEST_COMPILATION_NAME)
                val jvmTestRun = testRuns.named(DEFAULT_TEST_RUN_NAME)
                val jvmTest = jvmTestRun.flatMap { run -> run.executionTask }

                jvmTestCompilation.defaultSourceSet.apply {
                    kotlin.srcDir(generateTests.flatMap { it.outputDirectory })
                    resources.srcDir(prepareResources.flatMap { it.outputDirectory })
                }

                fun Test.configureCorpusTest(resultsDirectory: Provider<Directory>) {
                    group = GROUP
                    testClassesDirs = jvmTestCompilation.output.classesDirs
                    classpath = project.files(jvmTestCompilation.output.allOutputs, jvmTestCompilation.runtimeDependencyFiles)
                    javaLauncher.set(jvmTest.flatMap { test -> test.javaLauncher })
                    maxHeapSize = "2g"
                    jvmArgs("-Xss32m")
                    workingDirectory.set(project.layout.projectDirectory)
                    binaryResultsDirectory.set(resultsDirectory)
                    doFirst {
                        val test = this as Test
                        test.systemProperty(
                            GenerateCorpusTestsTask.RESULTS_DIRECTORY_PROPERTY,
                            test.binaryResultsDirectory.get().asFile.absolutePath,
                        )
                    }
                    inputs.property("corpusMachine", machineMetadata)
                    include("**/corpus/generated/**")
                }

                val corpusTest = project.tasks.register<Test>(TASK_NAME_CORPUS_TEST) {
                    description = "Runs the generated wasm-corpus tests against the JVM test runtime"
                    configureCorpusTest(extension.corpusResultsDirectory)
                }
                val generateCorpusReport = registerReport(
                    project = project,
                    name = TASK_NAME_GENERATE_REPORT,
                    description = "Aggregates wasm-corpus fixture timings into a JSON report",
                    extension = extension,
                    effectivePhase = effectivePhase,
                    machineMetadata = machineMetadata,
                    test = corpusTest,
                    reportFile = extension.corpusReportFile,
                )

                val corpusMeasurement = project.tasks.register<CorpusMeasurementTest>(TASK_NAME_CORPUS_MEASUREMENT) {
                    description = "Measures the generated wasm-corpus tests afresh for a baseline update"
                    configureCorpusTest(measurementResultsDirectory)
                }
                val generateMeasurementReport = registerReport(
                    project = project,
                    name = TASK_NAME_GENERATE_MEASUREMENT_REPORT,
                    description = "Aggregates a fresh wasm-corpus measurement into a JSON report",
                    extension = extension,
                    effectivePhase = effectivePhase,
                    machineMetadata = machineMetadata,
                    test = corpusMeasurement,
                    reportFile = measurementReportFile,
                )

                project.tasks.register(TASK_NAME_CORPUS) {
                    group = GROUP
                    description = "Runs wasm-corpus fixtures against the JVM test runtime"

                    dependsOn(corpusTest)
                    dependsOn(generateCorpusReport)
                }

                project.tasks.register<Copy>(TASK_NAME_UPDATE_BASELINE) {
                    group = GROUP
                    description = "Measures wasm-corpus afresh and updates the checked-in results baseline"

                    from(generateMeasurementReport.flatMap { task -> task.outputFile })
                    into(extension.corpusBaselineDirectory)
                    rename { BASELINE_FILE_NAME }
                }

                jvmTestRun.configure {
                    executionTask.configure {
                        maxHeapSize = "2g"
                        jvmArgs("-Xss32m")
                        exclude("**/corpus/generated/**")
                    }
                }

                project.tasks.matching { task -> task.name == TASK_NAME_TEST }.configureEach {
                    dependsOn(corpusTest)
                }
            }

            project.pluginManager.withPlugin(ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY_PLUGIN_ID) {
                project.extensions.getByType<KotlinMultiplatformExtension>().sourceSets
                    .matching { sourceSet -> sourceSet.name == ANDROID_DEVICE_TEST_SOURCE_SET_NAME }
                    .configureEach {
                        kotlin.srcDir(generateTests.flatMap { it.outputDirectory })
                        resources.srcDir(prepareResources.flatMap { it.outputDirectory })
                    }
            }
        }
    }

    private fun registerReport(
        project: Project,
        name: String,
        description: String,
        extension: WasmCorpusPluginExtension,
        effectivePhase: Provider<CorpusPhase>,
        machineMetadata: Provider<String>,
        test: TaskProvider<out Test>,
        reportFile: Provider<RegularFile>,
    ): TaskProvider<GenerateCorpusReportTask> = project.tasks.register<GenerateCorpusReportTask>(name) {
        group = GROUP
        this.description = description

        val testJavaLauncher = test.flatMap { task -> task.javaLauncher }
        resultFiles.from(
            test.flatMap { task -> task.binaryResultsDirectory }.map { directory ->
                directory.asFileTree.matching {
                    include("**/*.json")
                }
            },
        )
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
        testJavaLanguageVersion.set(testJavaLauncher.map { launcher -> launcher.metadata.languageVersion.toString() })
        testJavaRuntimeVersion.set(testJavaLauncher.map { launcher -> launcher.metadata.javaRuntimeVersion })
        testJvmVersion.set(testJavaLauncher.map { launcher -> launcher.metadata.jvmVersion })
        testJavaVendor.set(testJavaLauncher.map { launcher -> launcher.metadata.vendor })
        outputFile.set(reportFile)
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
        const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
        const val ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY_PLUGIN_ID = "com.android.kotlin.multiplatform.library"
        const val ANDROID_DEVICE_TEST_SOURCE_SET_NAME = "androidDeviceTest"
        const val PROP_PHASE = "wasmCorpus.phase"
        const val PROP_TARGETS = "wasmCorpus.targets"
        const val GROUP = "corpus"
        const val TASK_NAME_CORPUS = "corpus"
        const val TASK_NAME_CORPUS_TEST = "corpusTest"
        const val TASK_NAME_CORPUS_MEASUREMENT = "corpusMeasurementTest"
        const val TASK_NAME_TEST = "test"
        const val TASK_NAME_SYNC_CORPUS = "syncWasmCorpus"
        const val TASK_NAME_RESOLVE_FIXTURES = "resolveCorpusFixtures"
        const val TASK_NAME_GENERATE_TESTS = "generateCorpusTests"
        const val TASK_NAME_PREPARE_RESOURCES = "prepareCorpusResources"
        const val TASK_NAME_GENERATE_REPORT = "generateCorpusReport"
        const val TASK_NAME_GENERATE_MEASUREMENT_REPORT = "generateCorpusMeasurementReport"
        const val TASK_NAME_UPDATE_BASELINE = "updateCorpusBaseline"
        const val TASK_NAME_MATRIX = "corpusMatrix"
        const val TASK_NAME_CLEAN_TESTS = "cleanCorpusTests"
        const val DIR_MEASUREMENT_RESULTS = "wasm-corpus-measurement-results"
        const val FILE_MEASUREMENT_REPORT = "wasm-corpus-measurement-report.json"
        const val BASELINE_FILE_NAME = "baseline.json"
    }
}
