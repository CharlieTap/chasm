package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException

class DeviceReadiness(
    private val shell: DeviceShell,
) {

    fun check(device: ConnectedDevice) {

        shell.run("input", "keyevent", "KEYCODE_WAKEUP")

        val awake = AWAKE in shell.run("dumpsys", "power").text
        if (!awake) {
            throw GradleException("${device.model} did not wake")
        }

        val locked = shell.run("dumpsys", "trust").text.lineSequence()
            .any { line -> CURRENT_USER in line && LOCKED in line }
        if (locked) {
            throw GradleException("Unlock the ${device.model} before running the corpus")
        }

        val powered = POWERED.containsMatchIn(shell.run("dumpsys", "battery").text)
        if (!powered) {
            throw GradleException("Put the ${device.model} on power: stay-awake only holds while it charges")
        }

        val thermal = THERMAL_STATUS.find(shell.run("dumpsys", "thermalservice").text)?.groupValues?.get(1)
        if (thermal != COOL) {
            throw GradleException("${device.model} reports thermal status $thermal; let it cool")
        }
    }

    private companion object {
        const val AWAKE = "mWakefulness=Awake"
        const val CURRENT_USER = "(current)"
        const val LOCKED = "deviceLocked=1"
        const val COOL = "0"
        val POWERED = Regex("(AC|USB|Wireless|Dock) powered: true")
        val THERMAL_STATUS = Regex("Thermal Status: (\\d+)")
    }
}
