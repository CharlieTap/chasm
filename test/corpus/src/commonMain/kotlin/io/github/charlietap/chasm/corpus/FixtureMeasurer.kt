package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.corpus.engine.Engine
import io.github.charlietap.chasm.corpus.fixture.FixtureContext
import io.github.charlietap.chasm.corpus.fixture.TestRunner
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusSettings
import io.github.charlietap.corpus.lib.fixture.Fixture
import kotlinx.coroutines.runBlocking
import kotlin.time.TimeSource

internal class FixtureMeasurer(
    private val engine: Engine,
    private val settings: CorpusSettings,
    private val config: RuntimeConfig,
    private val rounds: Rounds,
    private val testRunner: TestRunner,
) : Measurer {

    override fun measure(
        fixture: Fixture,
        bytes: ByteArray,
    ): MeasuredFixture {

        val start = TimeSource.Monotonic.markNow()
        val coldRecorder = CorpusTimingRecorder()
        val context = FixtureContext(fixture, settings.phase, engine, config, coldRecorder)
        val module = prepare(context, bytes)
        val result = exercise(context, module)
        val cold = coldRecorder.timings(start.elapsedNow().inWholeNanoseconds)

        val warm = if (result == CorpusResult.Success) {
            rounds.warm { timings -> round(fixture, bytes, module, timings) }
        } else {
            null
        }

        return MeasuredFixture(result, measurement(fixture, engine.name(), bytes, coldRecorder.moduleBuildCount, result, cold, warm))
    }

    private fun round(
        fixture: Fixture,
        bytes: ByteArray,
        module: Module,
        timings: CorpusTimingRecorder,
    ): CorpusResult {

        val context = FixtureContext(fixture, settings.phase, engine, config, timings)
        val subject = if (settings.phase == CorpusPhase.INSTANTIATION) {
            prepare(context, bytes)
        } else {
            module
        }

        return exercise(context, subject)
    }

    private fun prepare(
        context: FixtureContext,
        bytes: ByteArray,
    ): Module {
        return context.timings.module {
            runBlocking { engine.module(bytes, config) }
        }
    }

    private fun exercise(
        context: FixtureContext,
        module: Module,
    ): CorpusResult {

        val fixture = context.fixture
        if (fixture.tests.isEmpty()) {
            return testRunner(context, module, null)
        }

        fixture.tests.withIndex().forEach { test ->
            val result = testRunner(context, module, test)
            if (result != CorpusResult.Success) {
                return result
            }
        }

        return CorpusResult.Success
    }
}
