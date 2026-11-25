package io.github.charlietap.chasm.corpus

import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture
import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.MeasurementWriter
import io.github.charlietap.corpus.lib.measurement.Timings
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ChasmCorpusRunnerTest {

    @Test
    fun `every measurement is written on the fixture thread before the result is returned`() {

        val writer = RecordingMeasurementWriter()
        val thread = CountingFixtureThread()
        val runner = runner(listOf(FixedMeasurer("interpreter", CorpusResult.Success), FixedMeasurer("compiled", SKIPPED)), writer, thread)

        val result = runner.run()

        assertEquals(SKIPPED, result)
        assertEquals(listOf("interpreter", "compiled"), writer.measurements.map { measurement -> measurement.engine })
        assertEquals(listOf("empty", "empty"), writer.measurements.map { measurement -> measurement.fixture })
        assertEquals(1, thread.runs)
    }

    @Test
    fun `a failure wins over a skip`() {

        val runner = runner(
            listOf(FixedMeasurer("interpreter", SKIPPED), FixedMeasurer("compiled", FAILURE)),
            RecordingMeasurementWriter(),
            CountingFixtureThread(),
        )

        val result = runner.run()

        assertEquals(FAILURE, result)
    }

    private fun runner(
        measurers: List<Measurer>,
        writer: MeasurementWriter,
        thread: FixtureThread,
    ): ChasmCorpusRunner {
        return ChasmCorpusRunner(
            version = "1.0",
            path = "empty.wasm",
            measurers = measurers,
            reader = MapCorpusFileReader(),
            writer = writer,
            thread = thread,
            json = Json,
        )
    }

    private class FixedMeasurer(
        private val engine: String,
        private val result: CorpusResult,
    ) : Measurer {

        override fun measure(
            fixture: Fixture,
            bytes: ByteArray,
        ): MeasuredFixture {

            val timings = Timings(0, 0, 0, 0, 0, 0)

            return MeasuredFixture(result, measurement(fixture, engine, bytes, 0, result, timings, null))
        }
    }

    private class RecordingMeasurementWriter : MeasurementWriter {

        val measurements = mutableListOf<Measurement>()

        override fun write(measurement: Measurement) {
            measurements += measurement
        }
    }

    private class CountingFixtureThread : FixtureThread {

        var runs = 0
            private set

        override fun <T> run(body: () -> T): T {

            runs++

            return body()
        }
    }

    private class MapCorpusFileReader : CorpusFileReader {

        override fun readText(path: String): String {
            return mapOf("corpus/1.0/empty.json" to """{"name":"empty","path":"empty.wasm"}""").getValue(path)
        }

        override fun readBytes(path: String): ByteArray {
            return mapOf("corpus/1.0/empty.wasm" to EMPTY_MODULE).getValue(path)
        }
    }

    private companion object {
        val SKIPPED = CorpusResult.Skipped("empty", "table imports are not supported yet: env.table")
        val FAILURE = CorpusResult.Failure("empty", "instantiate", "failed to instantiate wasm")
    }
}
