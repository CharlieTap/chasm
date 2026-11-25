package io.github.charlietap.corpus.plugin.device

import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusMachine
import io.github.charlietap.corpus.lib.report.CorpusRuntime
import io.github.charlietap.corpus.plugin.ANDROID_DEVICE
import org.gradle.api.GradleException

class DeviceProbe(
    private val shell: DeviceShell,
) {

    fun environment(conditions: Map<String, String>): CorpusEnvironment {

        val properties = properties(shell.run("getprop").text)
        val processor = listOfNotNull(properties[SOC_MANUFACTURER], properties[SOC_MODEL])
            .joinToString(" ")
            .ifEmpty { null }

        val machine = CorpusMachine(
            operatingSystem = ANDROID,
            operatingSystemVersion = "${properties.getValue(RELEASE)} (SDK ${properties.getValue(SDK)})",
            architecture = properties.getValue(ABI),
            model = properties.getValue(MODEL),
            processor = processor,
            availableProcessors = processors(),
            totalMemoryBytes = totalMemoryBytes(),
        )
        val runtime = CorpusRuntime(
            name = ART,
            version = artVersion(),
            detail = properties[FINGERPRINT],
        )

        return CorpusEnvironment(
            name = ANDROID_DEVICE,
            machine = machine,
            runtime = runtime,
            conditions = conditions,
        )
    }

    private fun properties(text: String): Map<String, String> {
        return text.lineSequence()
            .mapNotNull { line -> PROPERTY.matchEntire(line.trim()) }
            .associate { match -> match.groupValues[1] to match.groupValues[2] }
            .filterValues { value -> value.isNotEmpty() }
    }

    private fun processors(): Int {

        val output = shell.run("nproc", "--all").text.trim()

        return output.toIntOrNull() ?: throw GradleException("The device reported $output processors")
    }

    private fun totalMemoryBytes(): Long {

        val kilobytes = MEMORY_TOTAL.find(shell.run("cat", "/proc/meminfo").text)?.groupValues?.get(1)
            ?: throw GradleException("The device did not report MemTotal in /proc/meminfo")

        return kilobytes.toLong() * KILOBYTE
    }

    private fun artVersion(): String {
        return shell.run("pm", "list", "packages", "--apex-only", "--show-versioncode").text.lineSequence()
            .map { line -> line.trim().removePrefix(PACKAGE_PREFIX) }
            .firstOrNull { line -> line.substringBefore(' ').endsWith(ART_MODULE_SUFFIX) }
            ?.replace(VERSION_CODE, " ")
            ?: NO_ART_MODULE
    }

    private companion object {
        const val ANDROID = "Android"
        const val ART = "ART"
        const val NO_ART_MODULE = "no ART module listed"

        const val RELEASE = "ro.build.version.release"
        const val SDK = "ro.build.version.sdk"
        const val ABI = "ro.product.cpu.abi"
        const val MODEL = "ro.product.model"
        const val SOC_MANUFACTURER = "ro.soc.manufacturer"
        const val SOC_MODEL = "ro.soc.model"
        const val FINGERPRINT = "ro.build.fingerprint"

        const val PACKAGE_PREFIX = "package:"
        const val ART_MODULE_SUFFIX = ".art"
        const val VERSION_CODE = " versionCode:"
        const val KILOBYTE = 1024L

        val PROPERTY = Regex("\\[(.+?)]: \\[(.*)]")
        val MEMORY_TOTAL = Regex("MemTotal:\\s+(\\d+) kB")
    }
}
