package io.github.charlietap.corpus.plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.tools.r8.Version
import io.github.charlietap.corpus.plugin.task.DeviceCorpusTask
import io.github.charlietap.corpus.plugin.task.NonDebuggableManifestTask
import io.github.charlietap.corpus.plugin.task.ReleaseR8ClassesTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import java.time.Duration

class AndroidDeviceEnvironment(
    private val project: Project,
    private val extension: WasmCorpusPluginExtension,
    private val environments: Environments,
) {

    fun register() {

        val components = project.extensions.getByType<KotlinMultiplatformAndroidComponentsExtension>()

        components.onVariants { variant ->
            variant.deviceTests.values.forEach { deviceTest ->

                val manifestTask = project.tasks.register<NonDebuggableManifestTask>(TASK_MANIFEST)
                deviceTest.artifacts.use(manifestTask)
                    .wiredWithFiles(NonDebuggableManifestTask::mergedManifest, NonDebuggableManifestTask::updatedManifest)
                    .toTransform(SingleArtifact.MERGED_MANIFEST)

                val r8Task = project.tasks.register<ReleaseR8ClassesTask>(TASK_R8) {
                    bootClasspath.set(components.sdkComponents.bootClasspath)
                    keepRules.from(extension.deviceKeepRules)
                    r8Version.set(Version.getVersionString())
                }
                deviceTest.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                    .use(r8Task)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        ReleaseR8ClassesTask::jars,
                        ReleaseR8ClassesTask::directories,
                        ReleaseR8ClassesTask::output,
                    )

                val measurementsDirectory = extension.corpusMeasurementsDirectory.dir(ANDROID_DEVICE)
                val prebuiltApk = project.providers.gradleProperty(PROP_APK)
                val root = project.layout.settingsDirectory
                val runTask = project.tasks.register<DeviceCorpusTask>(TASK_RUN) {
                    group = GROUP
                    description = "Runs the wasm-corpus on an attached Android device"

                    adbExecutable.set(components.sdkComponents.adb)
                    if (prebuiltApk.isPresent) {
                        testApkFile.set(prebuiltApk.map { path -> root.file(path) })
                    } else {
                        testApkDirectory.set(deviceTest.artifacts.get(SingleArtifact.APK))
                    }
                    testApplicationId.set(deviceTest.applicationId)
                    instrumentationRunner.set(deviceTest.instrumentationRunner)
                    testClass.set(extension.testPackageName.map { name -> "$name.CorpusTest" })
                    requestedDevice.set(
                        project.providers.gradleProperty(PROP_DEVICE).orElse(project.providers.environmentVariable(ANDROID_SERIAL)),
                    )
                    outputDirectory.set(measurementsDirectory)
                    timeout.set(Duration.ofMinutes(TIMEOUT_MINUTES))
                    doNotTrackState("a corpus run is a measurement")
                    finalizedBy("corpusReport${environments.suffix(ANDROID_DEVICE)}")
                }

                environments.register(
                    name = ANDROID_DEVICE,
                    engines = setOf(INTERPRETER, COMPILED),
                    test = runTask,
                    environment = measurementsDirectory.map { directory -> directory.file(DeviceCorpusTask.ENVIRONMENT_FILE) },
                    runFile = measurementsDirectory.map { directory -> directory.file(DeviceCorpusTask.RUN_FILE) },
                    fixturesIndex = measurementsDirectory.map { directory -> directory.file(DeviceCorpusTask.FIXTURES_INDEX) },
                )
            }
        }
    }

    private companion object {
        const val TASK_MANIFEST = "corpusNonDebuggableManifest"
        const val TASK_R8 = "corpusReleaseR8Classes"
        const val TASK_RUN = "corpusAndroidDeviceRun"
        const val PROP_APK = "wasmCorpus.apk"
        const val PROP_DEVICE = "wasmCorpus.device"
        const val ANDROID_SERIAL = "ANDROID_SERIAL"
        const val TIMEOUT_MINUTES = 15L
    }
}
