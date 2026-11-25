package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.corpus.engine.engines
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.CorpusSettings
import io.github.charlietap.corpus.lib.ResourceCorpusFileReader
import io.github.charlietap.corpus.lib.measurement.FileMeasurementWriter
import kotlinx.io.files.Path
import kotlinx.serialization.json.Json

actual class PlatformCorpusRunner actual constructor(
    version: String,
    path: String,
    settings: CorpusSettings,
) : CorpusRunner {

    private val runner = ChasmCorpusRunner(
        version = version,
        path = path,
        measurers = measurers(engines, settings),
        reader = ResourceCorpusFileReader(),
        writer = FileMeasurementWriter(Path(checkNotNull(System.getenv(MEASUREMENTS))), Json),
        thread = LargeStackThread(),
        json = Json { ignoreUnknownKeys = true },
    )

    actual override fun run(): CorpusResult {
        return runner.run()
    }

    private companion object {
        const val MEASUREMENTS = "CORPUS_MEASUREMENTS"
    }
}
