package io.github.charlietap.corpus.plugin.device

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.MeasurementWriter
import kotlinx.serialization.json.Json
import java.io.File

class MeasurementFileWriter(
    private val directory: File,
) : MeasurementWriter {

    override fun write(measurement: Measurement) {

        val engineDirectory = directory.resolve(measurement.engine)
        engineDirectory.mkdirs()

        engineDirectory.resolve("${measurement.fixture}.json").writeText(Json.encodeToString(Measurement.serializer(), measurement))
    }
}
