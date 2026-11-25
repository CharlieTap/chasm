package io.github.charlietap.corpus.lib.measurement

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlinx.serialization.json.Json

class FileMeasurementWriter(
    private val directory: Path,
    private val json: Json,
) : MeasurementWriter {

    override fun write(measurement: Measurement) {

        val engineDirectory = Path(directory, measurement.engine)
        SystemFileSystem.createDirectories(engineDirectory)

        SystemFileSystem.sink(Path(engineDirectory, "${measurement.fixture}.json")).buffered().use { sink ->
            sink.writeString(json.encodeToString(Measurement.serializer(), measurement))
        }
    }
}
