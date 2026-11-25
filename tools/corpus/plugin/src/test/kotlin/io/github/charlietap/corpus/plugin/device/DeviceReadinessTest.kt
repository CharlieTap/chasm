package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeviceReadinessTest {

    @Test
    fun `an awake, unlocked, powered and cool device is ready`() {

        val adb = FakeAdb(responses())

        DeviceReadiness(AdbDeviceShell(adb, SERIAL)).check(DEVICE)

        assertEquals(
            listOf(
                "-s $SERIAL shell input keyevent KEYCODE_WAKEUP",
                "-s $SERIAL shell dumpsys power",
                "-s $SERIAL shell dumpsys trust",
                "-s $SERIAL shell dumpsys battery",
                "-s $SERIAL shell dumpsys thermalservice",
            ),
            adb.commands,
        )
    }

    @Test
    fun `refuses a device that did not wake`() {

        val failure = failure(power = "  mWakefulness=Asleep\n")

        assertEquals("SM-S721B did not wake", failure.message)
    }

    @Test
    fun `refuses a locked device`() {

        val failure = failure(
            trust = "User \"Owner\" (id=0, flags=0x13) (current): trusted=0, trustManaged=0, deviceLocked=1, strongAuthRequired=0x0\n",
        )

        assertEquals("Unlock the SM-S721B before running the corpus", failure.message)
    }

    @Test
    fun `refuses a device that is not on power`() {

        val failure = failure(battery = BATTERY.replace("USB powered: true", "USB powered: false"))

        assertEquals("Put the SM-S721B on power: stay-awake only holds while it charges", failure.message)
    }

    @Test
    fun `refuses a device that is not thermally cool`() {

        val failure = failure(thermal = "IsStatusOverride: false\nThermal Status: 2\n")

        assertEquals("SM-S721B reports thermal status 2; let it cool", failure.message)
    }

    private fun failure(
        power: String = POWER,
        trust: String = TRUST,
        battery: String = BATTERY,
        thermal: String = THERMAL,
    ): GradleException {

        val shell = AdbDeviceShell(FakeAdb(responses(power, trust, battery, thermal)), SERIAL)

        return assertFailsWith<GradleException> {
            DeviceReadiness(shell).check(DEVICE)
        }
    }

    private fun responses(
        power: String = POWER,
        trust: String = TRUST,
        battery: String = BATTERY,
        thermal: String = THERMAL,
    ): Map<String, AdbOutput> {
        return mapOf(
            "-s $SERIAL shell input keyevent KEYCODE_WAKEUP" to AdbOutput(0, ""),
            "-s $SERIAL shell dumpsys power" to AdbOutput(0, power),
            "-s $SERIAL shell dumpsys trust" to AdbOutput(0, trust),
            "-s $SERIAL shell dumpsys battery" to AdbOutput(0, battery),
            "-s $SERIAL shell dumpsys thermalservice" to AdbOutput(0, thermal),
        )
    }

    private companion object {
        const val SERIAL = "R5CXB04Y57E"
        val DEVICE = ConnectedDevice(SERIAL, SERIAL, "SM-S721B")

        const val POWER = "POWER MANAGER (dumpsys power)\n  mWakefulness=Awake\n  mWakefulnessChanging=false\n"
        const val TRUST =
            "Trust manager state:\n User \"Owner\" (id=0, flags=0x13) (current): trusted=0, trustManaged=0, deviceLocked=0, " +
                "strongAuthRequired=0x0\n"
        const val BATTERY =
            "Current Battery Service state:\n  AC powered: false\n  USB powered: true\n  Wireless powered: false\n" +
                "  Dock powered: false\n  level: 80\n  temperature: 291\n"
        const val THERMAL = "IsStatusOverride: false\nThermal Status: 0\n"
    }
}
