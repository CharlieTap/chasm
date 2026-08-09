package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.lib.report.CorpusMachine
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.management.ManagementFactory
import javax.inject.Inject

abstract class CorpusMachineValueSource : ValueSource<String, ValueSourceParameters.None> {

    @get:Inject
    abstract val cli: ExecOperations

    override fun obtain(): String {
        val operatingSystem = System.getProperty("os.name")
        val machine = CorpusMachine(
            operatingSystem = operatingSystem,
            operatingSystemVersion = System.getProperty("os.version"),
            architecture = System.getProperty("os.arch"),
            model = when {
                operatingSystem.startsWith("Mac", ignoreCase = true) -> commandOutput("sysctl", "-n", "hw.model")
                operatingSystem.startsWith("Linux", ignoreCase = true) -> readFirstLine(
                    "/sys/devices/virtual/dmi/id/product_name",
                )
                else -> null
            },
            processor = when {
                operatingSystem.startsWith("Mac", ignoreCase = true) -> commandOutput(
                    "sysctl",
                    "-n",
                    "machdep.cpu.brand_string",
                )
                operatingSystem.startsWith("Linux", ignoreCase = true) -> linuxProcessor()
                operatingSystem.startsWith("Windows", ignoreCase = true) -> System.getenv("PROCESSOR_IDENTIFIER")
                else -> null
            },
            availableProcessors = Runtime.getRuntime().availableProcessors(),
            totalMemoryBytes = totalMemoryBytes(),
        )
        return Json.encodeToString(machine)
    }

    private fun commandOutput(vararg command: String): String? = runCatching {
        val output = ByteArrayOutputStream()
        val result = cli.exec {
            commandLine(*command)
            standardOutput = output
            errorOutput = output
            isIgnoreExitValue = true
        }
        output.toString().trim().takeIf { result.exitValue == 0 && it.isNotEmpty() }
    }.getOrNull()

    private fun linuxProcessor(): String? = runCatching {
        File("/proc/cpuinfo").useLines { lines ->
            lines.firstNotNullOfOrNull { line ->
                line.takeIf {
                    it.startsWith("model name") || it.startsWith("Hardware")
                }?.substringAfter(':')?.trim()?.takeIf(String::isNotEmpty)
            }
        }
    }.getOrNull()

    private fun readFirstLine(path: String): String? = runCatching {
        File(path).useLines { lines -> lines.firstOrNull()?.trim()?.takeIf(String::isNotEmpty) }
    }.getOrNull()

    private fun totalMemoryBytes(): Long? =
        (ManagementFactory.getOperatingSystemMXBean() as? com.sun.management.OperatingSystemMXBean)
            ?.totalMemorySize
}
