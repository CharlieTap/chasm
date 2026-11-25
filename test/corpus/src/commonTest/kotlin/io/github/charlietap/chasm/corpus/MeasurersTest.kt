package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.corpus.engine.COMPILED
import io.github.charlietap.chasm.corpus.engine.INTERPRETER
import io.github.charlietap.chasm.corpus.engine.Interpreter
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MeasurersTest {

    @Test
    fun `front end phases measure once under the frontend name whatever the engines`() {

        val engines = setOf(Interpreter(), NamedEngine(COMPILED, Interpreter()))

        val decodingSettings = CorpusSettings(CorpusPhase.DECODING, setOf(COMPILED), warmups = 0, rounds = 0, pin = false)
        val validationSettings = CorpusSettings(CorpusPhase.VALIDATION, setOf(INTERPRETER), warmups = 0, rounds = 0, pin = false)

        val decoding = measurers(engines, decodingSettings)
        val validation = measurers(engines, validationSettings)

        assertEquals(listOf("frontend"), decoding.map { measurer -> measurer.measure(fixture(), EMPTY_MODULE).measurement.engine })
        assertEquals(listOf("frontend"), validation.map { measurer -> measurer.measure(fixture(), EMPTY_MODULE).measurement.engine })
    }

    @Test
    fun `engine phases measure only the selected engines`() {

        val engines = setOf(Interpreter(), NamedEngine(COMPILED, Interpreter()))
        val settings = CorpusSettings(CorpusPhase.INVOCATION, setOf(COMPILED), warmups = 0, rounds = 0, pin = false)

        val measurers = measurers(engines, settings)

        assertEquals(listOf(COMPILED), measurers.map { measurer -> measurer.measure(fixture(), EMPTY_MODULE).measurement.engine })
    }

    @Test
    fun `a selection that matches no engine on the platform fails`() {

        val engines = setOf(Interpreter())
        val settings = CorpusSettings(CorpusPhase.INSTANTIATION, setOf(COMPILED), warmups = 0, rounds = 0, pin = false)

        val failure = assertFailsWith<IllegalArgumentException> {
            measurers(engines, settings)
        }

        assertEquals("none of [compiled] runs here; this platform has [interpreter]", failure.message)
    }
}
