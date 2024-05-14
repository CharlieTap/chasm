package io.github.charlietap.sweet.plugin

import io.github.charlietap.sweet.plugin.task.GenerateTestsTask
import io.github.charlietap.sweet.plugin.task.PrepareTestSuiteTask
import io.github.charlietap.sweet.plugin.task.ResolveWast2JsonTask
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

        val resolveWast2JsonTask = project.tasks.register<ResolveWast2JsonTask>(
            TASK_NAME_RESOLVE_W2J,
        ) {
            description = TASK_DESCRIPTION_RESOLVE_W2J
            group = GROUP

            wabtVersion.set(extension.wabtVersion)
            outputFile.set(project.layout.buildDirectory.file("wast2json"))
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
            wast2Json.set(resolveWast2JsonTask.flatMap { it.outputFile })
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

        project.afterEvaluate {
            val kotlinExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

            kotlinExtension.sourceSets.getByName("commonTest") {
                kotlin.srcDir(extension.testSuiteTestsDirectory)
            }
        }
    }

    private companion object {

        const val URL_TESTSUITE = "https://github.com/WebAssembly/testsuite.git"

        const val GROUP = "testsuite"

        const val TASK_NAME_SYNC_SUITE = "syncWasmTestSuite"
        const val TASK_NAME_RESOLVE_W2J = "resolveWast2Json"
        const val TASK_NAME_PREPARE_SUITE = "prepareTestSuite"
        const val TASK_NAME_GENERATE_TESTS = "generateTests"

        const val TASK_DESCRIPTION_SYNC_SUITE = "Clones/Updates the wasm test suite to the given commit"
        const val TASK_DESCRIPTION_RESOLVE_W2J = "Resolves for wastjson on the local filesystem"
        const val TASK_DESCRIPTION_PREPARE_SUITE = "Prepare the wasm test suite for generation by running wast2json"
        const val TASK_DESCRIPTION_GENERATE_TESTS = "Generate tests from the web assembly testsuite"
    }
}
