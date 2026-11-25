package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException

data class ConnectedDevice(
    val serial: String,
    val hardware: String,
    val model: String,
)

class DeviceSelector(
    private val adb: Adb,
) {

    fun select(requested: String?): ConnectedDevice {

        val devices = transports(adb.run("devices", "-l").text, requested)
            .groupBy(Transport::hardware)
            .values
            .toList()
        val matching = if (requested == null) {
            devices.filterNot { transports -> transports.any(Transport::emulator) }.ifEmpty { devices }
        } else {
            devices.filter { transports -> transports.any { transport -> transport.matches(requested) } }
        }

        val selected = matching.singleOrNull() ?: throw GradleException(describe(devices, matching, requested))

        return device(selected)
    }

    private fun transports(
        listing: String,
        requested: String?,
    ): List<Transport> {
        return listing.lineSequence()
            .map { line -> line.trim().split(Regex("\\s+")) }
            .filter { fields -> fields.size > 1 && fields[1] == DEVICE_STATE }
            .mapNotNull { fields -> transport(fields, requested) }
            .toList()
    }

    private fun transport(
        fields: List<String>,
        requested: String?,
    ): Transport? {

        val serial = fields[0]
        val emulator = serial.startsWith(EMULATOR_PREFIX)
        val properties = if (emulator) {
            listOf(MODEL_PROPERTY)
        } else {
            listOf(SERIAL_PROPERTY, MODEL_PROPERTY)
        }
        val readings = properties.associateWith { property -> adb.run("-s", serial, "shell", "getprop", property) }
        val unreadable = readings.entries.firstOrNull { (_, output) -> output.exitCode != 0 || output.text.isBlank() }

        if (unreadable != null) {
            if (requested == null || requested == serial) {
                throw GradleException("Reading ${unreadable.key} from $serial failed: ${unreadable.value.text.trim()}")
            }
            return null
        }

        val hardware = if (emulator) {
            serial
        } else {
            readings.getValue(SERIAL_PROPERTY).text.trim()
        }

        return Transport(
            serial = serial,
            hardware = hardware,
            model = readings.getValue(MODEL_PROPERTY).text.trim(),
            usb = fields.any { field -> field.startsWith(USB_FIELD) },
            emulator = emulator,
        )
    }

    private fun device(transports: List<Transport>): ConnectedDevice {

        val preferred = transports.firstOrNull(Transport::usb) ?: transports.first()

        return ConnectedDevice(preferred.serial, preferred.hardware, preferred.model)
    }

    private fun describe(
        devices: List<List<Transport>>,
        matching: List<List<Transport>>,
        requested: String?,
    ): String {
        return when {
            devices.isEmpty() -> "No Android device is attached"
            matching.isEmpty() -> "No attached device matches $requested; attached: ${list(devices)}. $DEVICE_PROPERTY_HINT"
            else -> "Several Android devices match: ${list(matching)}. $DEVICE_PROPERTY_HINT"
        }
    }

    private fun list(devices: List<List<Transport>>): String {
        return devices.map { transports -> device(transports) }.joinToString { device -> "${device.model} (${device.serial})" }
    }

    private data class Transport(
        val serial: String,
        val hardware: String,
        val model: String,
        val usb: Boolean,
        val emulator: Boolean,
    ) {

        fun matches(requested: String): Boolean {
            return requested == serial || requested == hardware || requested == model
        }
    }

    private companion object {
        const val DEVICE_STATE = "device"
        const val USB_FIELD = "usb:"
        const val EMULATOR_PREFIX = "emulator-"
        const val SERIAL_PROPERTY = "ro.serialno"
        const val MODEL_PROPERTY = "ro.product.model"
        const val DEVICE_PROPERTY_HINT = "Pass -PwasmCorpus.device=<serial or model>"
    }
}
