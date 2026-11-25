package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.plugin.task.WriteCorpusEnvironmentTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.TEST_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests.Companion.DEFAULT_TEST_RUN_NAME
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeHostTest
import java.io.File

class KotlinEnvironments(
    private val project: Project,
    private val extension: WasmCorpusPluginExtension,
    private val fixtures: RegisteredFixtures,
    private val environments: Environments,
    private val hostEnvironments: HostEnvironments,
) {

    fun register() {

        val kotlin = project.extensions.getByType<KotlinMultiplatformExtension>()

        kotlin.sourceSets.named(KotlinSourceSet.COMMON_TEST_SOURCE_SET_NAME).configure {
            this.kotlin.srcDir(fixtures.generateTests.flatMap { task -> task.outputDirectory })
        }

        kotlin.targets.withType<KotlinJvmTarget>().configureEach {

            compilations.getByName(TEST_COMPILATION_NAME).defaultSourceSet.resources
                .srcDir(fixtures.prepareResources.flatMap { task -> task.outputDirectory })

            val testRun = testRuns.named(DEFAULT_TEST_RUN_NAME)
            val testTask = testRun.flatMap { run -> run.executionTask }
            testRun.configure {
                executionTask.configure {
                    measure(this, JVM)
                    environment(MEASUREMENTS, measurementsPath(JVM))
                    minHeapSize = TEST_HEAP_SIZE
                    maxHeapSize = TEST_HEAP_SIZE
                }
            }

            register(JVM, HOST_ENGINES, testTask, hostEnvironments.jvm(JVM, testTask.flatMap { task -> task.javaLauncher }))
        }

        kotlin.targets.withType<KotlinNativeTargetWithHostTests>().configureEach {

            val target = targetName
            val testTaskName = "$target$NATIVE_TEST_TASK_SUFFIX"

            project.tasks.withType<KotlinNativeHostTest>().named { taskName -> taskName == testTaskName }.configureEach {
                measure(this, target)
                environment(MEASUREMENTS, measurementsPath(target))
            }
            binaries.withType<TestExecutable>().configureEach {
                val copyResources = nativeResourcesTask(target, name)
                project.tasks.withType<Copy>().named { taskName -> taskName == copyResources }.configureEach {
                    from(fixtures.prepareResources.flatMap { task -> task.outputDirectory })
                }
            }

            register(target, NATIVE_ENGINES, testTaskName, hostEnvironments.native(target, project.getKotlinPluginVersion()))
        }

        project.pluginManager.withPlugin(ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY_PLUGIN_ID) {

            project.tasks.withType<Test>().named { taskName -> taskName == ANDROID_HOST_TEST }.configureEach {
                measure(this, ANDROID_HOST)
                environment(MEASUREMENTS, measurementsPath(ANDROID_HOST))
                minHeapSize = TEST_HEAP_SIZE
                maxHeapSize = TEST_HEAP_SIZE
            }

            register(ANDROID_HOST, HOST_ENGINES, ANDROID_HOST_TEST, hostEnvironments.jvm(ANDROID_HOST, androidHostLauncher()))
        }
    }

    private fun register(
        name: String,
        engines: Set<String>,
        testTask: Any,
        environment: Provider<String>,
    ) {

        val environmentTask = project.tasks.register<WriteCorpusEnvironmentTask>("corpusEnvironment${environments.suffix(name)}") {
            group = GROUP
            description = "Describes the $name corpus environment"

            this.environment.set(environment)
            output.set(project.layout.buildDirectory.file("$ENVIRONMENTS_DIRECTORY/$name.json"))
        }

        environments.register(
            name = name,
            engines = engines,
            test = testTask,
            environment = environmentTask.flatMap { task -> task.output },
            runFile = fixtures.prepareResources.flatMap { task -> task.runFile },
            fixturesIndex = fixtures.prepareResources.flatMap { task -> task.fixturesIndex },
        )
    }

    private fun measure(
        task: Task,
        environment: String,
    ) {

        val directory = measurementsPath(environment)

        task.doNotTrackState("a corpus run is a measurement")
        task.finalizedBy("corpusReport${environments.suffix(environment)}")
        task.doFirst {
            File(directory).deleteRecursively()
            File(directory).mkdirs()
        }
    }

    private fun measurementsPath(environment: String): String {
        return extension.corpusMeasurementsDirectory.dir(environment).get().asFile.absolutePath
    }

    private fun nativeResourcesTask(
        target: String,
        binary: String,
    ): String {
        return "$target${binary.replaceFirstChar(Char::uppercase)}$COPY_RESOURCES"
    }

    private fun androidHostLauncher(): Provider<JavaLauncher> {

        val tasks = project.tasks

        return project.provider { tasks.named<Test>(ANDROID_HOST_TEST).get() }.flatMap { task -> task.javaLauncher }
    }

    private companion object {
        const val ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY_PLUGIN_ID = "com.android.kotlin.multiplatform.library"
        const val ANDROID_HOST_TEST = "testAndroidHostTest"
        const val MEASUREMENTS = "CORPUS_MEASUREMENTS"
        const val TEST_HEAP_SIZE = "2g"
        const val ENVIRONMENTS_DIRECTORY = "corpus/environments"
        const val NATIVE_TEST_TASK_SUFFIX = "Test"
        const val COPY_RESOURCES = "CopyResources"
        val HOST_ENGINES = setOf(INTERPRETER, COMPILED)
        val NATIVE_ENGINES = setOf(INTERPRETER)
    }
}
