package io.github.charlietap.corpus.plugin.task

import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.plugin.device.AdbDeviceShell
import io.github.charlietap.corpus.plugin.device.ApkCorpusFiles
import io.github.charlietap.corpus.plugin.device.DeviceConditions
import io.github.charlietap.corpus.plugin.device.DeviceProbe
import io.github.charlietap.corpus.plugin.device.DeviceReadiness
import io.github.charlietap.corpus.plugin.device.DeviceSelector
import io.github.charlietap.corpus.plugin.device.DeviceShell
import io.github.charlietap.corpus.plugin.device.ExecAdb
import io.github.charlietap.corpus.plugin.device.InstrumentationOutput
import io.github.charlietap.corpus.plugin.device.InstrumentationProgress
import io.github.charlietap.corpus.plugin.device.MeasurementFileWriter
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Runs on a connected device")
abstract class DeviceCorpusTask
    @Inject
    constructor(
        private val execOperations: ExecOperations,
    ) : DefaultTask() {

        @get:InputFile
        @get:PathSensitive(PathSensitivity.NONE)
        abstract val adbExecutable: RegularFileProperty

        @get:InputDirectory
        @get:Optional
        @get:PathSensitive(PathSensitivity.NONE)
        abstract val testApkDirectory: DirectoryProperty

        @get:InputFile
        @get:Optional
        @get:PathSensitive(PathSensitivity.NONE)
        abstract val testApkFile: RegularFileProperty

        @get:Input
        abstract val testApplicationId: Property<String>

        @get:Input
        abstract val instrumentationRunner: Property<String>

        @get:Input
        abstract val testClass: Property<String>

        @get:Input
        @get:Optional
        abstract val requestedDevice: Property<String>

        @get:OutputDirectory
        abstract val outputDirectory: DirectoryProperty

        @TaskAction
        fun run() {

            val output = outputDirectory.get().asFile
            output.deleteRecursively()
            output.mkdirs()

            val apk = apk()
            val apkFiles = ApkCorpusFiles(apk)
            output.resolve(RUN_FILE).writeText(apkFiles.runJson())
            output.resolve(FIXTURES_INDEX).writeText(apkFiles.fixturesIndexJson())

            val adb = ExecAdb(execOperations, adbExecutable.get().asFile.path)
            val device = DeviceSelector(adb).select(requestedDevice.orNull)
            val shell = AdbDeviceShell(adb, device.serial)
            val conditions = DeviceConditions(shell)
            val instrumentationOutput = InstrumentationOutput(MeasurementFileWriter(output))
            val progress = InstrumentationProgress(logger)

            DeviceReadiness(shell).check(device)
            logger.lifecycle("Running the wasm-corpus on ${device.model} (${device.serial})")
            shell.install(apk)

            var runFailure: Throwable? = null
            try {
                logger.lifecycle("Holding ${device.model} awake in fixed-performance mode and compiling the test APK with speed")
                conditions.apply(testApplicationId.get())
                shell.instrument(testClass.get(), testApplicationId.get(), instrumentationRunner.get()) { line ->
                    instrumentationOutput.accept(line)
                    progress.report(instrumentationOutput)
                }
            } catch (failure: Throwable) {
                runFailure = failure
                throw failure
            } finally {
                val interrupted = Thread.interrupted()
                val cleanupFailures = restoreAndRecordEnvironment(shell, conditions, instrumentationOutput, output)
                if (interrupted) {
                    Thread.currentThread().interrupt()
                }
                reportCleanupFailures(runFailure, cleanupFailures)
            }

            if (!instrumentationOutput.completed || instrumentationOutput.failures.isNotEmpty()) {
                throw GradleException(instrumentationOutput.describeFailure())
            }
        }

        private fun restoreAndRecordEnvironment(
            shell: DeviceShell,
            conditions: DeviceConditions,
            instrumentationOutput: InstrumentationOutput,
            output: File,
        ): List<Exception> {

            val failures = mutableListOf<Exception>()

            try {
                conditions.restore()
            } catch (failure: Exception) {
                failures += failure
            }

            try {
                val recorded = conditions.recordedConditions() + instrumentationOutput.threadConditions()
                val environment = DeviceProbe(shell).environment(recorded)
                output.resolve(ENVIRONMENT_FILE).writeText(Json.encodeToString(CorpusEnvironment.serializer(), environment))
            } catch (failure: Exception) {
                failures += failure
            }

            return failures
        }

        private fun reportCleanupFailures(
            runFailure: Throwable?,
            cleanupFailures: List<Exception>,
        ) {

            if (runFailure != null) {
                cleanupFailures.forEach { failure -> runFailure.addSuppressed(failure) }
                return
            }

            val first = cleanupFailures.firstOrNull()
            if (first != null) {
                cleanupFailures.drop(1).forEach { failure -> first.addSuppressed(failure) }
                throw first
            }
        }

        private fun apk(): File {
            return if (testApkFile.isPresent) {
                testApkFile.get().asFile
            } else {
                testApkDirectory.asFileTree.matching { include("*.apk") }.singleFile
            }
        }

        companion object {
            const val ENVIRONMENT_FILE = "environment.json"
            const val RUN_FILE = "run.json"
            const val FIXTURES_INDEX = "fixtures.json"
        }
    }
