package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeviceSelectorTest {

    @Test
    fun `selects the only attached device when none is requested`() {

        val selector = selector(S24_USB)

        val device = selector.select(null)

        assertEquals(ConnectedDevice(S24_SERIAL, S24_SERIAL, "SM-S721B"), device)
    }

    @Test
    fun `treats the usb and wireless transports of one phone as one device and prefers usb`() {

        val selector = selector(S24_WIRELESS, S24_USB)

        val device = selector.select(null)

        assertEquals(ConnectedDevice(S24_SERIAL, S24_SERIAL, "SM-S721B"), device)
    }

    @Test
    fun `treats the mdns and ip transports of one wireless phone as one device`() {

        val selector = selector(PIXEL_WIRELESS, PIXEL_IP)

        val device = selector.select(null)

        assertEquals(ConnectedDevice(PIXEL_WIRELESS_SERIAL, PIXEL_SERIAL, PIXEL_MODEL), device)
    }

    @Test
    fun `prefers an attached phone over an emulator`() {

        val selector = selector(EMULATOR, S24_USB)

        val device = selector.select(null)

        assertEquals(S24_SERIAL, device.serial)
    }

    @Test
    fun `selects an emulator when it is the only device attached`() {

        val selector = selector(EMULATOR)

        val device = selector.select(null)

        assertEquals(ConnectedDevice(EMULATOR_SERIAL, EMULATOR_SERIAL, EMULATOR_MODEL), device)
    }

    @Test
    fun `selects a requested emulator beside an attached phone`() {

        val selector = selector(EMULATOR, S24_USB)

        val device = selector.select(EMULATOR_SERIAL)

        assertEquals(EMULATOR_SERIAL, device.serial)
    }

    @Test
    fun `ignores devices that are unauthorized or offline`() {

        val selector = selector(S24_USB, "emulator-5556          offline", "R3CT30KAMQX            unauthorized usb:1-2 transport_id:7")

        val device = selector.select(null)

        assertEquals(S24_SERIAL, device.serial)
    }

    @Test
    fun `selects a requested device by model, hardware serial or the serial of any of its transports`() {

        val selector = selector(S24_USB, S24_WIRELESS, PIXEL_WIRELESS, PIXEL_IP)

        assertEquals(S24_SERIAL, selector.select("SM-S721B").serial)
        assertEquals(S24_SERIAL, selector.select(S24_SERIAL).serial)
        assertEquals(S24_SERIAL, selector.select(S24_WIRELESS_SERIAL).serial)
        assertEquals(PIXEL_WIRELESS_SERIAL, selector.select(PIXEL_SERIAL).serial)
        assertEquals(PIXEL_WIRELESS_SERIAL, selector.select(PIXEL_IP_SERIAL).serial)
        assertEquals(PIXEL_WIRELESS_SERIAL, selector.select(PIXEL_MODEL).serial)
    }

    @Test
    fun `names a device by the model it reports, as the corpus report does`() {

        val selector = selector(PIXEL_WIRELESS)

        val failure = assertFailsWith<GradleException> {
            selector.select("Pixel-8-Pro")
        }

        assertEquals(
            "No attached device matches Pixel-8-Pro; attached: $PIXEL_MODEL ($PIXEL_WIRELESS_SERIAL). " +
                "Pass -PwasmCorpus.device=<serial or model>",
            failure.message,
        )
    }

    @Test
    fun `refuses to choose between several phones and names the property to pass`() {

        val selector = selector(S24_USB, S24_WIRELESS, PIXEL_WIRELESS, PIXEL_IP, EMULATOR)

        val failure = assertFailsWith<GradleException> {
            selector.select(null)
        }

        assertEquals(
            "Several Android devices match: SM-S721B ($S24_SERIAL), $PIXEL_MODEL ($PIXEL_WIRELESS_SERIAL). " +
                "Pass -PwasmCorpus.device=<serial or model>",
            failure.message,
        )
    }

    @Test
    fun `fails when the requested device is not attached`() {

        val selector = selector(S24_USB)

        val failure = assertFailsWith<GradleException> {
            selector.select(PIXEL_MODEL)
        }

        assertEquals(
            "No attached device matches $PIXEL_MODEL; attached: SM-S721B ($S24_SERIAL). Pass -PwasmCorpus.device=<serial or model>",
            failure.message,
        )
    }

    @Test
    fun `fails when no device is attached`() {

        val selector = selector()

        val failure = assertFailsWith<GradleException> {
            selector.select(null)
        }

        assertEquals("No Android device is attached", failure.message)
    }

    @Test
    fun `fails when a transport does not report its serial number`() {

        val adb = FakeAdb(
            mapOf(
                "devices -l" to AdbOutput(0, listing(PIXEL_IP)),
                "-s $PIXEL_IP_SERIAL shell getprop ro.serialno" to AdbOutput(1, "error: closed\n"),
                "-s $PIXEL_IP_SERIAL shell getprop ro.product.model" to AdbOutput(1, "error: closed\n"),
            ),
        )

        val failure = assertFailsWith<GradleException> {
            DeviceSelector(adb).select(null)
        }

        assertEquals("Reading ro.serialno from $PIXEL_IP_SERIAL failed: error: closed", failure.message)
    }

    @Test
    fun `fails when the requested transport does not report its model`() {

        val responses = properties() + mapOf(
            "devices -l" to AdbOutput(0, listing(S24_USB, PIXEL_IP)),
            "-s $PIXEL_IP_SERIAL shell getprop ro.product.model" to AdbOutput(0, "\n"),
        )

        val failure = assertFailsWith<GradleException> {
            DeviceSelector(FakeAdb(responses)).select(PIXEL_IP_SERIAL)
        }

        assertEquals("Reading ro.product.model from $PIXEL_IP_SERIAL failed: ", failure.message)
    }

    @Test
    fun `a transport that cannot be read does not stop another device being selected`() {

        val responses = properties() + mapOf(
            "devices -l" to AdbOutput(0, listing(S24_USB, PIXEL_IP)),
            "-s $PIXEL_IP_SERIAL shell getprop ro.serialno" to AdbOutput(1, "error: closed\n"),
        )

        val device = DeviceSelector(FakeAdb(responses)).select("SM-S721B")

        assertEquals(ConnectedDevice(S24_SERIAL, S24_SERIAL, "SM-S721B"), device)
    }

    private fun selector(vararg devices: String): DeviceSelector {
        return DeviceSelector(FakeAdb(properties() + ("devices -l" to AdbOutput(0, listing(*devices)))))
    }

    private fun properties(): Map<String, AdbOutput> {

        val hardware = mapOf(
            S24_SERIAL to S24_SERIAL,
            S24_WIRELESS_SERIAL to S24_SERIAL,
            PIXEL_WIRELESS_SERIAL to PIXEL_SERIAL,
            PIXEL_IP_SERIAL to PIXEL_SERIAL,
        )
        val models = mapOf(
            S24_SERIAL to "SM-S721B",
            S24_WIRELESS_SERIAL to "SM-S721B",
            PIXEL_WIRELESS_SERIAL to PIXEL_MODEL,
            PIXEL_IP_SERIAL to PIXEL_MODEL,
            EMULATOR_SERIAL to EMULATOR_MODEL,
        )
        val serials = hardware.entries.associate { (transport, serial) ->
            "-s $transport shell getprop ro.serialno" to AdbOutput(0, "$serial\n")
        }
        val products = models.entries.associate { (transport, model) ->
            "-s $transport shell getprop ro.product.model" to AdbOutput(0, "$model\n")
        }

        return serials + products
    }

    private fun listing(vararg devices: String): String {
        return (listOf("List of devices attached") + devices + "").joinToString("\n")
    }

    private companion object {
        const val S24_SERIAL = "R5CXB04Y57E"
        const val S24_USB = "$S24_SERIAL            device usb:1-1 product:r12sxxx model:SM_S721B device:r12s transport_id:3"
        const val S24_WIRELESS_SERIAL = "adb-R5CXB04Y57E-AbCdEf._adb-tls-connect._tcp"
        const val S24_WIRELESS = "$S24_WIRELESS_SERIAL device product:r12sxxx model:SM_S721B device:r12s transport_id:4"
        const val PIXEL_SERIAL = "3A111FDJG000BW"
        const val PIXEL_MODEL = "Pixel 8 Pro"
        const val PIXEL_WIRELESS_SERIAL = "adb-3A111FDJG000BW-QwErTy._adb-tls-connect._tcp"
        const val PIXEL_WIRELESS = "$PIXEL_WIRELESS_SERIAL device product:husky model:Pixel_8_Pro device:husky transport_id:5"
        const val PIXEL_IP_SERIAL = "192.168.1.23:37891"
        const val PIXEL_IP = "$PIXEL_IP_SERIAL     device product:husky model:Pixel_8_Pro device:husky transport_id:6"
        const val EMULATOR_SERIAL = "emulator-5554"
        const val EMULATOR_MODEL = "sdk_gphone64_arm64"
        const val EMULATOR = "$EMULATOR_SERIAL          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:1"
    }
}
