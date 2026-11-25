package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusSettings
import io.github.charlietap.corpus.lib.fixture.Fixture
import kotlinx.coroutines.runBlocking
import kotlin.time.TimeSource

internal class FrontEndMeasurer(
    private val settings: CorpusSettings,
    private val config: ModuleConfig,
    private val rounds: Rounds,
) : Measurer {

    override fun measure(
        fixture: Fixture,
        bytes: ByteArray,
    ): MeasuredFixture {

        val start = TimeSource.Monotonic.markNow()
        val coldRecorder = CorpusTimingRecorder()
        val result = pass(fixture, bytes, coldRecorder)
        val cold = coldRecorder.timings(start.elapsedNow().inWholeNanoseconds)

        val warm = if (result == CorpusResult.Success) {
            rounds.warm { timings -> pass(fixture, bytes, timings) }
        } else {
            null
        }

        return MeasuredFixture(result, measurement(fixture, FRONT_END, bytes, 0, result, cold, warm))
    }

    private fun pass(
        fixture: Fixture,
        bytes: ByteArray,
        timings: CorpusTimingRecorder,
    ): CorpusResult {

        val decoded = when (val result = timings.decode { runBlocking { module(bytes, config, validate = false) } }) {
            is ChasmResult.Success -> result.result
            is ChasmResult.Error -> {
                return fixture.failure("decode", "failed to decode wasm", result.error.toString())
            }
        }

        if (settings.phase == CorpusPhase.DECODING) {
            return CorpusResult.Success
        }

        return when (val result = timings.validate { runBlocking { validate(decoded) } }) {
            is ChasmResult.Success -> CorpusResult.Success
            is ChasmResult.Error -> fixture.failure("validate", "failed to validate wasm", result.error.toString())
        }
    }

    private companion object {
        const val FRONT_END = "frontend"
    }
}
