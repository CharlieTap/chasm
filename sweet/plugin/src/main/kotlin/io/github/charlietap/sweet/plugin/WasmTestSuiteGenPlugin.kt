package io.github.charlietap.sweet.plugin

import io.github.charlietap.sweet.plugin.task.DownloadWasmToolsTask
import io.github.charlietap.sweet.plugin.task.GenerateTestsTask
import io.github.charlietap.sweet.plugin.task.PrepareTestSuiteTask
import io.github.charlietap.sweet.plugin.task.ResolveWasmToolsTask
import io.github.charlietap.sweet.plugin.task.SyncRepositoryTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class WasmTestSuiteGenPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions
            .create<WasmTestSuiteGenPluginExtension>("chasm-testsuite-plugin-extension")

        val syncRepositoryTask = project.tasks.register<SyncRepositoryTask>(
            TASK_NAME_SYNC_SUITE,
        ) {
            description = TASK_DESCRIPTION_SYNC_SUITE
            group = GROUP

            repositoryUrl.set(URL_TESTSUITE)
            commitHash.set(extension.testSuiteCommit)
            outputDirectory.set(extension.testSuiteDirectory)
        }

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
            outputFile.set(wasmToolsVersion.zip(wasmToolsDirectory) { version, dir ->
                dir.dir(version).file("wasm-tools")
            })
        }

        val prepareTestSuiteTask = project.tasks.register<PrepareTestSuiteTask>(
            TASK_NAME_PREPARE_SUITE,
        ) {
            description = TASK_DESCRIPTION_PREPARE_SUITE
            group = GROUP

            inputFiles.apply {
                from(syncRepositoryTask.flatMap { it.outputDirectory })
                include("*.wast")
                extension.proposals.get().forEach { proposal ->
                    include("proposals/$proposal/*.wast")
                }
                exclude(extension.excludes.get())
                builtBy(syncRepositoryTask)
            }

            excludes.set(extension.excludes)
            proposals.set(extension.proposals)
            wast2Json.set(resolveWasmToolsTask.flatMap { it.outputFile })
            outputDirectory.set(extension.testSuiteIntermediateDirectory)
        }

        val generateTestsTask = project.tasks.register<GenerateTestsTask>(
            TASK_NAME_GENERATE_TESTS,
        ) {
            description = TASK_DESCRIPTION_GENERATE_TESTS
            group = GROUP

            inputFiles.apply {
                from(prepareTestSuiteTask.flatMap { it.outputDirectory })
                include("**/*.json")
                builtBy(prepareTestSuiteTask)
            }

            scriptRunner.set(extension.scriptRunner)
            testPackageName.set(extension.testPackageName)
            outputDirectory.set(extension.testSuiteTestsDirectory)
        }

        project.tasks.named {
            it.contains("compileTestKotlin")
        }.configureEach {
            dependsOn(generateTestsTask)
        }

        val kotlinExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

        kotlinExtension.sourceSets.getByName("commonTest") {
            kotlin.srcDir(extension.testSuiteTestsDirectory)
        }
    }

    private companion object {

        const val URL_TESTSUITE = "https://github.com/WebAssembly/testsuite.git"

        const val GROUP = "testsuite"

        const val TASK_NAME_SYNC_SUITE = "syncWasmTestSuite"
        const val TASK_NAME_DOWNLOAD_WASM_TOOLS = "downloadWasmTools"
        const val TASK_NAME_RESOLVE_WASM_TOOLS = "resolveWasmTools"
        const val TASK_NAME_PREPARE_SUITE = "prepareTestSuite"
        const val TASK_NAME_GENERATE_TESTS = "generateTests"

        const val TASK_DESCRIPTION_SYNC_SUITE = "Clones/Updates the wasm test suite to the given commit"
        const val TASK_DESCRIPTION_DOWNLOAD_WT = "Downloads and extracts the wasm-tools"
        const val TASK_DESCRIPTION_RESOLVE_WT = "Resolves wasm-tools on the local filesystem"
        const val TASK_DESCRIPTION_PREPARE_SUITE = "Prepare the wasm test suite for generation by running wast2json"
        const val TASK_DESCRIPTION_GENERATE_TESTS = "Generate tests from the web assembly testsuite"
    }
}
