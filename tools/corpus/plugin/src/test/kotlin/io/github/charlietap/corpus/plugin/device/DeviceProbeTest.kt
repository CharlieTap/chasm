package io.github.charlietap.corpus.plugin.device

import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusMachine
import io.github.charlietap.corpus.lib.report.CorpusRuntime
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceProbeTest {

    @Test
    fun `describes the phone, its ART module and the recorded conditions`() {

        val probe = DeviceProbe(AdbDeviceShell(FakeAdb(responses()), SERIAL))

        val environment = probe.environment(CONDITIONS)

        assertEquals(
            CorpusEnvironment(
                name = "androidDevice",
                machine = CorpusMachine(
                    operatingSystem = "Android",
                    operatingSystemVersion = "16 (SDK 36)",
                    architecture = "arm64-v8a",
                    model = "SM-S721B",
                    processor = "Samsung s5e9945",
                    availableProcessors = 10,
                    totalMemoryBytes = 7_462_144L * 1024,
                ),
                runtime = CorpusRuntime(
                    name = "ART",
                    version = "com.google.android.art 361234567",
                    detail = FINGERPRINT,
                ),
                conditions = CONDITIONS,
            ),
            environment,
        )
    }

    @Test
    fun `a device without system-on-chip properties reports no processor`() {

        val properties = PROPERTIES.lineSequence().filterNot { line -> line.startsWith("[ro.soc.") }.joinToString("\n")
        val probe = DeviceProbe(AdbDeviceShell(FakeAdb(responses(properties = properties)), SERIAL))

        val environment = probe.environment(emptyMap())

        assertEquals(null, environment.machine.processor)
    }

    @Test
    fun `a device that lists no ART module says so`() {

        val probe = DeviceProbe(AdbDeviceShell(FakeAdb(responses(modules = "package:com.android.tzdata versionCode:351010000\n")), SERIAL))

        val environment = probe.environment(emptyMap())

        assertEquals("no ART module listed", environment.runtime.version)
    }

    private fun responses(
        properties: String = PROPERTIES,
        modules: String = MODULES,
    ): Map<String, AdbOutput> {

        val shell = "-s $SERIAL shell"

        return mapOf(
            "$shell getprop" to AdbOutput(0, properties),
            "$shell nproc --all" to AdbOutput(0, "10\n"),
            "$shell cat /proc/meminfo" to AdbOutput(0, "MemTotal:        7462144 kB\nMemFree:          512000 kB\n"),
            "$shell pm list packages --apex-only --show-versioncode" to AdbOutput(0, modules),
        )
    }

    private companion object {
        const val SERIAL = "R5CXB04Y57E"
        const val FINGERPRINT = "samsung/r12sxxx/r12s:16/BP2A.250605.031/S721BXXU5CYK1:user/release-keys"
        val CONDITIONS = mapOf("fixedPerformanceMode" to "true", "threadPriority" to "-8", "allowedCpus" to "0-9")

        val PROPERTIES = listOf(
            "[ro.build.fingerprint]: [$FINGERPRINT]",
            "[ro.build.version.release]: [16]",
            "[ro.build.version.sdk]: [36]",
            "[ro.product.cpu.abi]: [arm64-v8a]",
            "[ro.product.manufacturer]: [samsung]",
            "[ro.product.model]: [SM-S721B]",
            "[ro.soc.manufacturer]: [Samsung]",
            "[ro.soc.model]: [s5e9945]",
            "[ro.vendor.empty]: []",
        ).joinToString("\n")

        val MODULES = listOf(
            "package:com.android.adbd versionCode:351010000",
            "package:com.google.android.artemis versionCode:1",
            "package:com.google.android.art versionCode:361234567",
        ).joinToString("\n")
    }
}
