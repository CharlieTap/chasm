package io.github.charlietap.chasm.corpus

import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.fixture.Fixture
import io.github.charlietap.corpus.lib.measurement.MeasurementWriter
import kotlinx.serialization.json.Json

class ChasmCorpusRunner(
    private val version: String,
    private val path: String,
    private val measurers: List<Measurer>,
    private val reader: CorpusFileReader,
    private val writer: MeasurementWriter,
    private val thread: FixtureThread,
    private val json: Json,
) : CorpusRunner {

    override fun run(): CorpusResult {

        val metadata = reader.readText("$CORPUS_ROOT/$version/${path.removeSuffix(".wasm")}.json")
        val fixture = json.decodeFromString(Fixture.serializer(), metadata).copy(version = version)
        val bytes = reader.readBytes("$CORPUS_ROOT/$version/$path")

        val results = thread.run {
            measurers.map { measurer ->
                val measured = measurer.measure(fixture, bytes)
                writer.write(measured.measurement)
                measured.result
            }
        }

        return results.firstOrNull { result -> result is CorpusResult.Failure }
            ?: results.firstOrNull { result -> result is CorpusResult.Skipped }
            ?: CorpusResult.Success
    }

    private companion object {
        const val CORPUS_ROOT = "corpus"
    }
}
