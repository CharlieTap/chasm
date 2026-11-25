package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.corpus.engine.INTERPRETER
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FrontEndMeasurerTest {

    @Test
    fun `decoding measures no validation`() {

        val settings = CorpusSettings(CorpusPhase.DECODING, setOf(INTERPRETER), warmups = 0, rounds = 0, pin = false)
        val measurer = FrontEndMeasurer(settings, ModuleConfig(), MedianRounds(settings))

        val measured = measurer.measure(fixture(), EMPTY_MODULE)

        assertEquals(CorpusResult.Success, measured.result)
        assertEquals(0, measured.measurement.cold.validateNanos)
    }

    @Test
    fun `a truncated module fails to decode`() {

        val settings = CorpusSettings(CorpusPhase.VALIDATION, setOf(INTERPRETER), warmups = 0, rounds = 0, pin = false)
        val measurer = FrontEndMeasurer(settings, ModuleConfig(), MedianRounds(settings))

        val measured = measurer.measure(fixture(), EMPTY_MODULE.copyOf(6))

        val failure = assertIs<CorpusResult.Failure>(measured.result)
        assertEquals("decode", failure.context)
    }
}
