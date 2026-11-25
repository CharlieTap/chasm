package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException
import java.io.File

class AdbDeviceShell(
    private val adb: Adb,
    private val serial: String,
) : DeviceShell {

    override fun run(vararg arguments: String): AdbOutput {
        return adb.run(SERIAL_FLAG, serial, SHELL, *arguments)
    }

    override fun install(apk: File) {

        val output = adb.run(SERIAL_FLAG, serial, "install", "-r", "-t", apk.path)

        if (output.exitCode != 0 || INSTALLED !in output.text) {
            throw GradleException("Installing ${apk.name} on $serial failed: ${output.text.trim()}")
        }
    }

    override fun instrument(
        testClass: String,
        applicationId: String,
        runner: String,
        line: (String) -> Unit,
    ): Int {

        val arguments = listOf(SERIAL_FLAG, serial, SHELL, "am", "instrument", "-w", "-r", "-e", "class", testClass, "$applicationId/$runner")

        return adb.stream(arguments, line)
    }

    private companion object {
        const val SERIAL_FLAG = "-s"
        const val SHELL = "shell"
        const val INSTALLED = "Success"
    }
}
