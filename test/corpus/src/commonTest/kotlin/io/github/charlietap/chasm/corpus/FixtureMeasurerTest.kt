package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.corpus.engine.INTERPRETER
import io.github.charlietap.chasm.corpus.engine.Interpreter
import io.github.charlietap.chasm.corpus.fixture.TestRunner
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FixtureMeasurerTest {

    @Test
    fun `invocation rounds reuse the module prepared for the cold pass`() {

        val engine = CountingEngine(Interpreter())
        val settings = CorpusSettings(CorpusPhase.INVOCATION, setOf(INTERPRETER), warmups = 1, rounds = 3, pin = false)
        val measurer = FixtureMeasurer(engine, settings, RuntimeConfig(), MedianRounds(settings), ::TestRunner)

        val measured = measurer.measure(fixture(), EMPTY_MODULE)

        assertEquals(CorpusResult.Success, measured.result)
        assertEquals(1, engine.moduleCount)
        assertEquals(5, engine.storeCount)
        assertNotNull(measured.measurement.warm)
    }

    @Test
    fun `instantiation rounds prepare the module again every round`() {

        val engine = CountingEngine(Interpreter())
        val settings = CorpusSettings(CorpusPhase.INSTANTIATION, setOf(INTERPRETER), warmups = 1, rounds = 3, pin = false)
        val measurer = FixtureMeasurer(engine, settings, RuntimeConfig(), MedianRounds(settings), ::TestRunner)

        measurer.measure(fixture(), EMPTY_MODULE)

        assertEquals(5, engine.moduleCount)
    }

    @Test
    fun `no rounds leaves the warm timings empty`() {

        val settings = CorpusSettings(CorpusPhase.INVOCATION, setOf(INTERPRETER), warmups = 0, rounds = 0, pin = false)
        val measurer = FixtureMeasurer(Interpreter(), settings, RuntimeConfig(), MedianRounds(settings), ::TestRunner)

        assertNull(measurer.measure(fixture(), EMPTY_MODULE).measurement.warm)
    }
}
