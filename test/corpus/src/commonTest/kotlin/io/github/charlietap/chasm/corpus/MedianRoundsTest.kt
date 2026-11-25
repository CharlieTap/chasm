package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.corpus.engine.INTERPRETER
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MedianRoundsTest {

    @Test
    fun `an odd number of rounds gives the middle sample after the warm-ups`() {

        val rounds = MedianRounds(settings(warmups = 1, rounds = 3))
        val executions = mutableListOf(100L, 30L, 10L, 20L)

        val warm = rounds.warm { timings ->
            timings.executeNanos = executions.removeAt(0)
            CorpusResult.Success
        }

        assertEquals(20, warm?.executeNanos)
        assertEquals(emptyList(), executions)
    }

    @Test
    fun `an even number of rounds gives the mean of the middle pair`() {

        val rounds = MedianRounds(settings(warmups = 0, rounds = 4))
        val executions = mutableListOf(10L, 40L, 20L, 30L)

        val warm = rounds.warm { timings ->
            timings.executeNanos = executions.removeAt(0)
            CorpusResult.Success
        }

        assertEquals(25, warm?.executeNanos)
    }

    @Test
    fun `a round that does not repeat the cold result fails`() {

        val rounds = MedianRounds(settings(warmups = 0, rounds = 1))

        assertFailsWith<IllegalStateException> {
            rounds.warm { _ -> CorpusResult.Skipped("empty", "skipped") }
        }
    }

    private fun settings(
        warmups: Int,
        rounds: Int,
    ): CorpusSettings {
        return CorpusSettings(CorpusPhase.INVOCATION, setOf(INTERPRETER), warmups, rounds, pin = false)
    }
}
