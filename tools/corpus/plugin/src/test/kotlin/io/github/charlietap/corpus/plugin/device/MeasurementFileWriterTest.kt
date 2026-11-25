package io.github.charlietap.corpus.plugin.device

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.Outcome
import io.github.charlietap.corpus.lib.measurement.Timings
import kotlinx.serialization.json.Json
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MeasurementFileWriterTest {

    private val directory = createTempDirectory("measurement-files").toFile()

    @AfterTest
    fun deleteDirectory() {
        directory.deleteRecursively()
    }

    @Test
    fun `writes each measurement to a file named after its fixture under its engine`() {

        val compiled = measurement("compiled")
        val interpreter = measurement("interpreter")

        val writer = MeasurementFileWriter(directory)
        writer.write(compiled)
        writer.write(interpreter)

        assertEquals(compiled, read("compiled/sqlite3.json"))
        assertEquals(interpreter, read("interpreter/sqlite3.json"))
    }

    private fun read(path: String): Measurement {
        return Json.decodeFromString(Measurement.serializer(), directory.resolve(path).readText())
    }

    private fun measurement(engine: String): Measurement {
        return Measurement(
            fixture = "sqlite3",
            version = "2.0",
            path = "sqlite3.wasm",
            sha256 = null,
            engine = engine,
            outcome = Outcome.Skipped,
            detail = "uses an excluded feature",
            binaryBytes = 1_048_576,
            tests = 0,
            steps = 0,
            instances = 0,
            cold = Timings(
                totalNanos = 0,
                decodeNanos = 0,
                validateNanos = 0,
                moduleNanos = 0,
                instantiateNanos = 0,
                executeNanos = 0,
            ),
            warm = null,
        )
    }
}
