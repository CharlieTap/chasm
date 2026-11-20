package io.github.charlietap.chasm.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@DisableCachingByDefault(because = "Runs a test on a connected device")
abstract class DeviceInstrumentationCheckTask @Inject constructor(
    private val execOperations: ExecOperations,
) : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val adbExecutable: RegularFileProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val testApkDirectory: DirectoryProperty

    @get:Input
    abstract val testApplicationId: Property<String>

    @get:Input
    abstract val instrumentationRunner: Property<String>

    @get:Input
    abstract val testClass: Property<String>

    @get:Input
    abstract val deviceModel: Property<String>

    @TaskAction
    fun check() {
        val model = deviceModel.get()
        val modelToken = "model:" + model.replace(Regex("[^A-Za-z0-9]"), "_")
        val serial = adb("devices", "-l").lineSequence()
            .firstOrNull { line -> modelToken in line.split(' ') }
            ?.substringBefore(' ')
            ?: throw GradleException("No connected device with model $model")
        val apk = testApkDirectory.asFileTree.matching { include("*.apk") }.singleFile

        val stayOnWhilePluggedIn = adb("-s", serial, "shell", "settings", "get", "global", "stay_on_while_plugged_in").trim()

        adb("-s", serial, "shell", "input", "keyevent", "KEYCODE_WAKEUP")
        val locked = adb("-s", serial, "shell", "dumpsys", "trust").lineSequence()
            .any { line -> "(current)" in line && "deviceLocked=1" in line }
        if (locked) throw GradleException("Unlock the $model before running this check")
        adb("-s", serial, "install", "-r", "-t", apk.path)

        val output = try {
            adb("-s", serial, "shell", "settings", "put", "global", "stay_on_while_plugged_in", "7")
            adb("-s", serial, "shell", "cmd", "power", "set-fixed-performance-mode-enabled", "true")
            adb(
                "-s", serial, "shell", "am", "instrument", "-w",
                "-e", "class", testClass.get(),
                "${testApplicationId.get()}/${instrumentationRunner.get()}",
            )
        } finally {
            try {
                adb("-s", serial, "shell", "cmd", "power", "set-fixed-performance-mode-enabled", "false")
            } finally {
                adb("-s", serial, "shell", "settings", "put", "global", "stay_on_while_plugged_in", stayOnWhilePluggedIn)
            }
        }
        if ("OK (1 test)" !in output) throw GradleException(output)
    }

    private fun adb(vararg arguments: String): String {
        val output = ByteArrayOutputStream()
        execOperations.exec {
            commandLine(adbExecutable.get().asFile.path, *arguments)
            standardOutput = output
        }
        return output.toString()
    }
}
