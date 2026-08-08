package io.github.charlietap.sweet.plugin

import io.github.charlietap.sweet.plugin.ext.toTaskNameSegment
import io.github.charlietap.sweet.plugin.task.DownloadWasmToolsTask
import io.github.charlietap.sweet.plugin.task.GenerateTestsTask
import io.github.charlietap.sweet.plugin.task.PrepareTestSuiteTask
import io.github.charlietap.sweet.plugin.task.ResolveWasmToolsTask
import io.github.charlietap.sweet.plugin.task.SyncRepositoryTask
import io.github.charlietap.sweet.plugin.task.TestMatrixTask
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.TEST_COMPILATION_NAME

class WasmTestSuiteGenPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create<WasmTestSuiteGenPluginExtension>("sweet")

        val downloadWasmToolsTask = project.tasks.register<DownloadWasmToolsTask>(
            TASK_NAME_DOWNLOAD_WASM_TOOLS,
        ) {
            description = TASK_DESCRIPTION_DOWNLOAD_WT
            group = GROUP

            wasmToolsVersion.set(extension.wasmToolsVersion)
            outputDirectory.set(project.layout.buildDirectory.dir("wasm-tools"))
        }

        val resolveWasmToolsTask = project.tasks.register<ResolveWasmToolsTask>(
            TASK_NAME_RESOLVE_WASM_TOOLS,
        ) {
            description = TASK_DESCRIPTION_RESOLVE_WT
            group = GROUP

            wasmToolsVersion.set(extension.wasmToolsVersion)
            wasmToolsDirectory.set(downloadWasmToolsTask.flatMap { it.outputDirectory })
            outputFile.set(
                wasmToolsVersion.zip(wasmToolsDirectory) { version, directory ->
                    directory.dir(version).file("wasm-tools")
                },
            )
        }

        val syncTestSuiteTask = project.tasks.register(TASK_NAME_SYNC_SUITE) {
            description = TASK_DESCRIPTION_SYNC_SUITE
            group = GROUP
        }

        val prepareTestSuiteTask = project.tasks.register(TASK_NAME_PREPARE_SUITE) {
            description = TASK_DESCRIPTION_PREPARE_SUITE
            group = GROUP
        }

        val generateTestsTask = project.tasks.register(TASK_NAME_GENERATE_TESTS) {
            description = TASK_DESCRIPTION_GENERATE_TESTS
            group = GROUP
        }

        project.tasks.register<TestMatrixTask>(TASK_NAME_MATRIX) {
            description = TASK_DESCRIPTION_MATRIX
            group = GROUP

            testFiles.from(extension.testsDirectory)
            testFiles.builtBy(generateTestsTask)
        }

        extension.sources.all(
            object : Action<SuiteSource> {
                override fun execute(source: SuiteSource) {
                    registerSourceTasks(
                        project = project,
                        extension = extension,
                        source = source,
                        resolveWasmToolsTask = resolveWasmToolsTask,
                        syncTestSuiteTask = syncTestSuiteTask,
                        prepareTestSuiteTask = prepareTestSuiteTask,
                        generateTestsTask = generateTestsTask,
                    )
                }
            },
        )

        project.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
            val kotlinExtension = project.extensions.getByType<KotlinMultiplatformExtension>()
            kotlinExtension.sourceSets.getByName("commonTest").kotlin.srcDir(extension.testsDirectory)
            kotlinExtension.targets.configureEach {
                compilations.configureEach {
                    if (name == TEST_COMPILATION_NAME) {
                        compileTaskProvider.configure {
                            dependsOn(generateTestsTask)
                        }
                    }
                }
            }
        }
    }

    private fun registerSourceTasks(
        project: Project,
        extension: WasmTestSuiteGenPluginExtension,
        source: SuiteSource,
        resolveWasmToolsTask: TaskProvider<ResolveWasmToolsTask>,
        syncTestSuiteTask: TaskProvider<Task>,
        prepareTestSuiteTask: TaskProvider<Task>,
        generateTestsTask: TaskProvider<Task>,
    ) {
        val sourceTaskName = source.name.toTaskNameSegment()

        val syncSourceTask = project.tasks.register<SyncRepositoryTask>("sync${sourceTaskName}TestSuite") {
            description = "Clones or updates the ${source.name} suite source to its configured revision"
            group = GROUP

            repositoryUrl.set(source.repositoryUrl)
            commitHash.set(source.revision)
            outputDirectory.set(extension.repositoriesDirectory.dir(source.name))
        }

        val sourceRoot = syncSourceTask.flatMap { it.outputDirectory }
            .zip(source.testDirectory) { repository, testDirectory ->
                repository.dir(testDirectory)
            }

        val selectedFiles = sourceRoot.zip(
            source.includes.zip(source.excludes) { includes, excludes -> includes to excludes },
        ) { root, (includes, excludes) ->
            root.asFileTree.matching {
                include(includes)
                exclude(excludes)
            }
        }

        val prepareSourceTask = project.tasks.register<PrepareTestSuiteTask>("prepare${sourceTaskName}TestSuite") {
            description = "Prepares the ${source.name} suite source with wasm-tools"
            group = GROUP

            inputFiles.from(selectedFiles)
            inputFiles.builtBy(syncSourceTask)
            sourceDirectory.set(sourceRoot)
            wast2Json.set(resolveWasmToolsTask.flatMap { it.outputFile })
            outputDirectory.set(extension.intermediatesDirectory.dir(source.name))
        }

        val preparedScripts = prepareSourceTask.flatMap { it.outputDirectory }.map { directory ->
            directory.asFileTree.matching {
                include("**/*.json")
            }
        }

        val generateSourceTask = project.tasks.register<GenerateTestsTask>("generate${sourceTaskName}Tests") {
            description = "Generates Kotlin tests for the ${source.name} suite source"
            group = GROUP

            inputFiles.from(preparedScripts)
            inputFiles.builtBy(prepareSourceTask)
            intermediateDirectory.set(prepareSourceTask.flatMap { it.outputDirectory })
            sourceName.set(source.name)
            phaseSupport.set(source.phaseSupport)
            phaseLimits.set(source.phaseLimits)
            scriptRunner.set(extension.scriptRunner)
            testPackageName.set(extension.testPackageName)
            outputDirectory.set(extension.testsDirectory.dir(source.name))
        }

        syncTestSuiteTask.configure {
            dependsOn(syncSourceTask)
        }
        prepareTestSuiteTask.configure {
            dependsOn(prepareSourceTask)
        }
        generateTestsTask.configure {
            dependsOn(generateSourceTask)
        }
    }

    private companion object {

        const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"

        const val GROUP = "sweet"

        const val TASK_NAME_SYNC_SUITE = "syncWasmTestSuite"
        const val TASK_NAME_DOWNLOAD_WASM_TOOLS = "downloadWasmTools"
        const val TASK_NAME_RESOLVE_WASM_TOOLS = "resolveWasmTools"
        const val TASK_NAME_PREPARE_SUITE = "prepareTestSuite"
        const val TASK_NAME_GENERATE_TESTS = "generateTests"
        const val TASK_NAME_MATRIX = "testMatrix"

        const val TASK_DESCRIPTION_SYNC_SUITE = "Synchronizes every configured wasm suite source"
        const val TASK_DESCRIPTION_DOWNLOAD_WT = "Downloads and extracts wasm-tools"
        const val TASK_DESCRIPTION_RESOLVE_WT = "Resolves wasm-tools on the local filesystem"
        const val TASK_DESCRIPTION_PREPARE_SUITE = "Prepares every wasm suite source with wasm-tools"
        const val TASK_DESCRIPTION_GENERATE_TESTS = "Generates tests from every configured wasm suite source"
        const val TASK_DESCRIPTION_MATRIX = "Generates a test matrix over the generated test files"
    }
}
