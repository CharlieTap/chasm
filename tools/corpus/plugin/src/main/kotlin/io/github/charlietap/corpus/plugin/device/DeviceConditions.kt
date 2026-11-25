package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException

class DeviceConditions(
    private val shell: DeviceShell,
) {

    private val recorded = linkedMapOf<String, String>()
    private var stayOnBefore: String? = null
    private var fixedPerformanceMode = false

    fun apply(applicationId: String) {

        val battery = shell.run("dumpsys", "battery").text
        recorded[DEBUGGABLE] = debuggable(applicationId).toString()
        recorded[POWERED] = powered(battery)
        recorded[THERMAL_STATUS_BEFORE] = thermalStatus()
        recorded[BATTERY_TEMPERATURE_BEFORE] = batteryTemperature(battery)

        stayOnBefore = runChecked("settings", "get", "global", STAY_ON).text.trim()
        runChecked("settings", "put", "global", STAY_ON, STAY_ON_ALL_SOURCES)

        fixedPerformanceMode = shell.run("cmd", "power", FIXED_PERFORMANCE_MODE_COMMAND, "true").exitCode == 0
        recorded[FIXED_PERFORMANCE_MODE] = fixedPerformanceMode.toString()

        val compile = shell.run("cmd", "package", "compile", "-m", SPEED, "-f", applicationId)
        if (compile.exitCode != 0 || SUCCESS !in compile.text) {
            throw GradleException("Compiling $applicationId with $SPEED failed: ${compile.text.trim()}")
        }
        recorded[COMPILER_FILTER] = SPEED
    }

    fun recordedConditions(): Map<String, String> {
        return recorded.toMap()
    }

    fun restore() {

        val failures = mutableListOf<GradleException>()

        if (fixedPerformanceMode) {
            fixedPerformanceMode = false
            failures += restoreSetting("disabling fixed-performance mode", "cmd", "power", FIXED_PERFORMANCE_MODE_COMMAND, "false")
        }

        val stayOn = stayOnBefore
        if (stayOn != null) {
            stayOnBefore = null
            failures += if (stayOn == UNSET) {
                restoreSetting("restoring $STAY_ON to $stayOn", "settings", "delete", "global", STAY_ON)
            } else {
                restoreSetting("restoring $STAY_ON to $stayOn", "settings", "put", "global", STAY_ON, stayOn)
            }
        }

        val failure = restoreFailure(failures)
        try {
            recorded[THERMAL_STATUS_AFTER] = thermalStatus()
            recorded[BATTERY_TEMPERATURE_AFTER] = batteryTemperature(shell.run("dumpsys", "battery").text)
        } catch (exception: Exception) {
            if (failure == null) {
                throw exception
            }
            failure.addSuppressed(exception)
        }

        if (failure != null) {
            throw failure
        }
    }

    private fun runChecked(vararg arguments: String): AdbOutput {

        val output = shell.run(*arguments)

        if (output.exitCode != 0) {
            throw GradleException("${arguments.joinToString(" ")} failed: ${output.text.trim()}")
        }

        return output
    }

    private fun restoreSetting(
        description: String,
        vararg arguments: String,
    ): List<GradleException> {
        return try {
            val output = shell.run(*arguments)
            if (output.exitCode == 0) {
                emptyList()
            } else {
                listOf(GradleException("$description: ${output.text.trim()}"))
            }
        } catch (exception: Exception) {
            listOf(GradleException("$description: ${exception.message}", exception))
        }
    }

    private fun restoreFailure(failures: List<GradleException>): GradleException? {

        if (failures.isEmpty()) {
            return null
        }

        val descriptions = failures.joinToString("; ") { setting -> setting.message.orEmpty() }
        val failure = GradleException("Restoring the device settings failed: $descriptions")
        failures.mapNotNull(Throwable::cause).forEach { cause -> failure.addSuppressed(cause) }

        return failure
    }

    private fun debuggable(applicationId: String): Boolean {
        return PACKAGE_DEBUGGABLE.containsMatchIn(shell.run("dumpsys", "package", applicationId).text)
    }

    private fun powered(battery: String): String {
        return POWER_SOURCE.findAll(battery)
            .map { match -> match.groupValues[1] }
            .joinToString(",")
            .ifEmpty { UNPOWERED }
    }

    private fun thermalStatus(): String {
        return reading(THERMAL_STATUS, shell.run("dumpsys", "thermalservice").text, "a thermal status")
    }

    private fun batteryTemperature(battery: String): String {
        return reading(BATTERY_TEMPERATURE, battery, "a battery temperature")
    }

    private fun reading(
        pattern: Regex,
        text: String,
        description: String,
    ): String {
        return pattern.find(text)?.groupValues?.get(1) ?: throw GradleException("The device did not report $description")
    }

    private companion object {
        const val STAY_ON = "stay_on_while_plugged_in"
        const val STAY_ON_ALL_SOURCES = "7"
        const val UNSET = "null"
        const val FIXED_PERFORMANCE_MODE_COMMAND = "set-fixed-performance-mode-enabled"
        const val SPEED = "speed"
        const val SUCCESS = "Success"
        const val UNPOWERED = "none"

        const val DEBUGGABLE = "debuggable"
        const val POWERED = "powered"
        const val COMPILER_FILTER = "compilerFilter"
        const val FIXED_PERFORMANCE_MODE = "fixedPerformanceMode"
        const val THERMAL_STATUS_BEFORE = "thermalStatusBefore"
        const val THERMAL_STATUS_AFTER = "thermalStatusAfter"
        const val BATTERY_TEMPERATURE_BEFORE = "batteryTemperatureBefore"
        const val BATTERY_TEMPERATURE_AFTER = "batteryTemperatureAfter"

        val PACKAGE_DEBUGGABLE = Regex("pkgFlags=\\[[^\\]]*\\bDEBUGGABLE\\b")
        val POWER_SOURCE = Regex("(AC|USB|Wireless|Dock) powered: true")
        val THERMAL_STATUS = Regex("Thermal Status: (\\d+)")
        val BATTERY_TEMPERATURE = Regex("temperature: (\\d+)")
    }
}
