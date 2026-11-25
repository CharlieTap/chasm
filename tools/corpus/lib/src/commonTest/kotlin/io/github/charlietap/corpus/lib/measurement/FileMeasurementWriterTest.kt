package io.github.charlietap.corpus.lib.measurement

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readString
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class FileMeasurementWriterTest {

    @Test
    fun `writes a measurement to a file named after its fixture under its engine`() {

        val directory = Path(SystemTemporaryDirectory, "corpus-measurements-${Random.nextLong().toULong()}")
        val engine = Path(directory, "compiled")
        val file = Path(engine, "activity_selection.json")
        val measurement = measurement()

        try {
            FileMeasurementWriter(directory, Json).write(measurement)

            val text = SystemFileSystem.source(file).buffered().use { source -> source.readString() }
            assertEquals(measurement, Json.decodeFromString(Measurement.serializer(), text))
        } finally {
            SystemFileSystem.delete(file, mustExist = false)
            SystemFileSystem.delete(engine, mustExist = false)
            SystemFileSystem.delete(directory, mustExist = false)
        }
    }

    private fun measurement(): Measurement {
        return Measurement(
            fixture = "activity_selection",
            version = "1.0",
            path = "activity_selection.wasm",
            sha256 = "99ba76e98cda4c68b9a6b5f36f8cead181be3cdb343a3b3096894e0862bafaf4",
            engine = "compiled",
            outcome = Outcome.Passed,
            detail = null,
            binaryBytes = 721,
            tests = 3,
            steps = 3,
            instances = 3,
            cold = Timings(
                totalNanos = 600,
                decodeNanos = 0,
                validateNanos = 0,
                moduleNanos = 300,
                instantiateNanos = 200,
                executeNanos = 100,
            ),
            warm = Timings(
                totalNanos = 60,
                decodeNanos = 0,
                validateNanos = 0,
                moduleNanos = 30,
                instantiateNanos = 20,
                executeNanos = 10,
            ),
        )
    }
}
