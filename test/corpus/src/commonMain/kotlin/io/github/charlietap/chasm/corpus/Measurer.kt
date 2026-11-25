package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.corpus.engine.Engine
import io.github.charlietap.chasm.corpus.fixture.TestRunner
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusSettings
import io.github.charlietap.corpus.lib.fixture.Fixture
import io.github.charlietap.corpus.lib.measurement.Measurement

interface Measurer {

    fun measure(
        fixture: Fixture,
        bytes: ByteArray,
    ): MeasuredFixture
}

data class MeasuredFixture(
    val result: CorpusResult,
    val measurement: Measurement,
)

fun measurers(
    engines: Set<Engine>,
    settings: CorpusSettings,
): List<Measurer> {

    val rounds = MedianRounds(settings)
    val measurers = when (settings.phase) {
        CorpusPhase.DECODING, CorpusPhase.VALIDATION -> listOf(FrontEndMeasurer(settings, ModuleConfig(), rounds))
        CorpusPhase.INSTANTIATION, CorpusPhase.INVOCATION ->
            engines
                .filter { engine -> engine.name() in settings.engines }
                .map { engine -> FixtureMeasurer(engine, settings, RuntimeConfig(), rounds, ::TestRunner) }
    }
    require(measurers.isNotEmpty()) {
        "none of ${settings.engines} runs here; this platform has ${engines.map { engine -> engine.name() }}"
    }

    return measurers
}
