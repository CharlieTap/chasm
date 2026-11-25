package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class DeviceConditionsTest {

    @Test
    fun `applies every condition, restores the settings and records what took effect`() {

        val adb = FakeAdb(responses())
        val conditions = DeviceConditions(AdbDeviceShell(adb, SERIAL))

        conditions.apply(PACKAGE)
        conditions.restore()

        assertEquals(
            mapOf(
                "debuggable" to "false",
                "powered" to "USB",
                "thermalStatusBefore" to "0",
                "batteryTemperatureBefore" to "291",
                "fixedPerformanceMode" to "true",
                "compilerFilter" to "speed",
                "thermalStatusAfter" to "0",
                "batteryTemperatureAfter" to "291",
            ),
            conditions.recordedConditions(),
        )
        assertEquals(
            listOf(
                "dumpsys battery",
                "dumpsys package $PACKAGE",
                "dumpsys thermalservice",
                "settings get global stay_on_while_plugged_in",
                "settings put global stay_on_while_plugged_in 7",
                "cmd power set-fixed-performance-mode-enabled true",
                "cmd package compile -m speed -f $PACKAGE",
                "cmd power set-fixed-performance-mode-enabled false",
                "settings put global stay_on_while_plugged_in 0",
                "dumpsys thermalservice",
                "dumpsys battery",
            ),
            adb.commands.map { command -> command.removePrefix("-s $SERIAL shell ") },
        )
    }

    @Test
    fun `a failed speed compile throws and restore still undoes stay-awake and fixed-performance mode`() {

        val adb = FakeAdb(responses(compile = AdbOutput(1, "Failure: package $PACKAGE could not be compiled\n")))
        val conditions = DeviceConditions(AdbDeviceShell(adb, SERIAL))

        val failure = assertFailsWith<GradleException> {
            conditions.apply(PACKAGE)
        }
        conditions.restore()
        val recorded = conditions.recordedConditions()

        assertEquals("Compiling $PACKAGE with speed failed: Failure: package $PACKAGE could not be compiled", failure.message)
        assertEquals("true", recorded["fixedPerformanceMode"])
        assertFalse("compilerFilter" in recorded)
        assertEquals(
            listOf(
                "cmd power set-fixed-performance-mode-enabled false",
                "settings put global stay_on_while_plugged_in 0",
            ),
            adb.commands.map { command -> command.removePrefix("-s $SERIAL shell ") }
                .dropWhile { command -> !command.startsWith("cmd package compile") }
                .drop(1)
                .take(2),
        )
    }

    @Test
    fun `a compile that exits cleanly without reporting success is a failure`() {

        val conditions = DeviceConditions(AdbDeviceShell(FakeAdb(responses(compile = AdbOutput(0, "Error: unknown\n"))), SERIAL))

        val failure = assertFailsWith<GradleException> {
            conditions.apply(PACKAGE)
        }

        assertEquals("Compiling $PACKAGE with speed failed: Error: unknown", failure.message)
    }

    @Test
    fun `fixed-performance mode the device refuses is recorded as false and not undone`() {

        val adb = FakeAdb(
            responses(
                fixedPerformanceMode = AdbOutput(255, "Failed to set FIXED_PERFORMANCE mode\n"),
            ),
        )
        val conditions = DeviceConditions(AdbDeviceShell(adb, SERIAL))

        conditions.apply(PACKAGE)
        conditions.restore()

        assertEquals("false", conditions.recordedConditions()["fixedPerformanceMode"])
        assertFalse("-s $SERIAL shell cmd power set-fixed-performance-mode-enabled false" in adb.commands)
    }

    @Test
    fun `an unset stay-awake setting is deleted again on restore`() {

        val adb = FakeAdb(responses(stayOn = AdbOutput(0, "null\n")))
        val conditions = DeviceConditions(AdbDeviceShell(adb, SERIAL))

        conditions.apply(PACKAGE)
        conditions.restore()

        assertEquals(
            listOf("settings delete global stay_on_while_plugged_in"),
            adb.commands.map { command -> command.removePrefix("-s $SERIAL shell ") }
                .filter { command -> command.startsWith("settings") && "global stay_on_while_plugged_in" in command }
                .drop(2),
        )
    }

    @Test
    fun `a debuggable package is recorded as debuggable`() {

        val conditions = DeviceConditions(
            AdbDeviceShell(FakeAdb(responses(packageFlags = "    pkgFlags=[ DEBUGGABLE HAS_CODE TEST_ONLY LARGE_HEAP ]\n")), SERIAL),
        )

        conditions.apply(PACKAGE)
        conditions.restore()

        assertEquals("true", conditions.recordedConditions()["debuggable"])
    }

    @Test
    fun `a stay-awake setting the device does not report stops before anything is changed`() {

        val adb = FakeAdb(responses(stayOn = AdbOutput(1, "Permission denial\n")))
        val conditions = DeviceConditions(AdbDeviceShell(adb, SERIAL))

        val failure = assertFailsWith<GradleException> {
            conditions.apply(PACKAGE)
        }
        conditions.restore()

        assertEquals("settings get global stay_on_while_plugged_in failed: Permission denial", failure.message)
        assertEquals(
            listOf("dumpsys thermalservice", "dumpsys battery"),
            adb.commands.map { command -> command.removePrefix("-s $SERIAL shell ") }
                .dropWhile { command -> command != "settings get global stay_on_while_plugged_in" }
                .drop(1),
        )
    }

    @Test
    fun `a settings change the device refuses stops before anything else is applied and is still undone`() {

        val adb = FakeAdb(responses(stayOnChange = AdbOutput(1, "Permission denial\n")))
        val conditions = DeviceConditions(AdbDeviceShell(adb, SERIAL))

        val failure = assertFailsWith<GradleException> {
            conditions.apply(PACKAGE)
        }
        conditions.restore()

        assertEquals("settings put global stay_on_while_plugged_in 7 failed: Permission denial", failure.message)
        assertEquals(
            listOf("settings put global stay_on_while_plugged_in 0", "dumpsys thermalservice", "dumpsys battery"),
            adb.commands.map { command -> command.removePrefix("-s $SERIAL shell ") }
                .dropWhile { command -> command != "settings put global stay_on_while_plugged_in 7" }
                .drop(1),
        )
    }

    @Test
    fun `restore reports a setting it could not restore after trying every one`() {

        val adb = FakeAdb(responses(stayOnRestore = AdbOutput(1, "Permission denial\n")))
        val conditions = DeviceConditions(AdbDeviceShell(adb, SERIAL))
        conditions.apply(PACKAGE)

        val failure = assertFailsWith<GradleException> {
            conditions.restore()
        }

        assertEquals(
            "Restoring the device settings failed: restoring stay_on_while_plugged_in to 0: Permission denial",
            failure.message,
        )
        assertEquals("-s $SERIAL shell cmd power set-fixed-performance-mode-enabled false", adb.commands[adb.commands.size - 4])
    }

    @Test
    fun `restore tries every setting when a command throws and still reads the device afterwards`() {

        val adb = FakeAdb(responses())
        val shell = ThrowingShell(AdbDeviceShell(adb, SERIAL), "cmd power set-fixed-performance-mode-enabled false")
        val conditions = DeviceConditions(shell)
        conditions.apply(PACKAGE)

        val failure = assertFailsWith<GradleException> {
            conditions.restore()
        }

        assertEquals("Restoring the device settings failed: disabling fixed-performance mode: $OFFLINE", failure.message)
        assertEquals(listOf(OFFLINE), failure.suppressed.map { suppressed -> suppressed.message })
        assertEquals(
            listOf("settings put global stay_on_while_plugged_in 0", "dumpsys thermalservice", "dumpsys battery"),
            adb.commands.map { command -> command.removePrefix("-s $SERIAL shell ") }
                .dropWhile { command -> !command.startsWith("cmd package compile") }
                .drop(1),
        )
        assertEquals("0", conditions.recordedConditions()["thermalStatusAfter"])
    }

    private fun responses(
        stayOn: AdbOutput = AdbOutput(0, "0\n"),
        stayOnChange: AdbOutput = AdbOutput(0, ""),
        stayOnRestore: AdbOutput = AdbOutput(0, ""),
        fixedPerformanceMode: AdbOutput = AdbOutput(0, ""),
        compile: AdbOutput = AdbOutput(0, "Success\n"),
        packageFlags: String = "    pkgFlags=[ HAS_CODE TEST_ONLY LARGE_HEAP ]\n",
    ): Map<String, AdbOutput> {

        val shell = "-s $SERIAL shell"

        return mapOf(
            "$shell dumpsys battery" to AdbOutput(0, BATTERY),
            "$shell dumpsys package $PACKAGE" to AdbOutput(0, "Packages:\n  Package [$PACKAGE] (1a2b3c):\n$packageFlags"),
            "$shell dumpsys thermalservice" to AdbOutput(0, "IsStatusOverride: false\nThermal Status: 0\n"),
            "$shell settings get global stay_on_while_plugged_in" to stayOn,
            "$shell settings put global stay_on_while_plugged_in 7" to stayOnChange,
            "$shell settings put global stay_on_while_plugged_in 0" to stayOnRestore,
            "$shell settings delete global stay_on_while_plugged_in" to AdbOutput(0, "Deleted 1 rows\n"),
            "$shell cmd power set-fixed-performance-mode-enabled true" to fixedPerformanceMode,
            "$shell cmd power set-fixed-performance-mode-enabled false" to AdbOutput(0, ""),
            "$shell cmd package compile -m speed -f $PACKAGE" to compile,
        )
    }

    private class ThrowingShell(
        private val shell: DeviceShell,
        private val failingCommand: String,
    ) : DeviceShell {

        override fun run(vararg arguments: String): AdbOutput {

            if (arguments.joinToString(" ") == failingCommand) {
                throw IllegalStateException(OFFLINE)
            }

            return shell.run(*arguments)
        }

        override fun install(apk: File) {
            shell.install(apk)
        }

        override fun instrument(
            testClass: String,
            applicationId: String,
            runner: String,
            line: (String) -> Unit,
        ): Int {
            return shell.instrument(testClass, applicationId, runner, line)
        }
    }

    private companion object {
        const val SERIAL = "R5CXB04Y57E"
        const val PACKAGE = "io.github.charlietap.chasm.corpus.test"
        const val OFFLINE = "error: device offline"
        const val BATTERY =
            "Current Battery Service state:\n  AC powered: false\n  USB powered: true\n  Wireless powered: false\n" +
                "  Dock powered: false\n  level: 80\n  temperature: 291\n"
    }
}
