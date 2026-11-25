package io.github.charlietap.chasm.corpus

import androidx.test.platform.app.InstrumentationRegistry
import io.github.charlietap.chasm.corpus.engine.engines
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.CorpusSettings
import io.github.charlietap.corpus.lib.ResourceCorpusFileReader
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
        writer = InstrumentationMeasurementWriter(InstrumentationRegistry.getInstrumentation(), Json),
        thread = MeasurementThread(
            thread = LargeStackThread(),
            scheduling = AndroidThreadScheduling(),
            conditions = InstrumentationThreadConditions(InstrumentationRegistry.getInstrumentation()),
            pin = settings.pin,
        ),
        json = Json { ignoreUnknownKeys = true },
    )

    actual override fun run(): CorpusResult {
        return runner.run()
    }
}
