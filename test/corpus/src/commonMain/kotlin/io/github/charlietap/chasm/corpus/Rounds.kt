package io.github.charlietap.chasm.corpus

import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusSettings
import io.github.charlietap.corpus.lib.measurement.Timings
import kotlin.time.TimeSource

internal interface Rounds {

    fun warm(pass: (CorpusTimingRecorder) -> CorpusResult): Timings?
}

internal class MedianRounds(
    private val settings: CorpusSettings,
) : Rounds {

    override fun warm(pass: (CorpusTimingRecorder) -> CorpusResult): Timings? {

        if (settings.rounds == 0) {
            return null
        }

        repeat(settings.warmups) {
            sample(pass)
        }

        val samples = List(settings.rounds) {
            sample(pass)
        }

        return median(samples)
    }

    private fun sample(pass: (CorpusTimingRecorder) -> CorpusResult): Timings {

        val start = TimeSource.Monotonic.markNow()
        val timings = CorpusTimingRecorder()
        val result = pass(timings)
        check(result == CorpusResult.Success) { "a round did not repeat the cold result: $result" }

        return timings.timings(start.elapsedNow().inWholeNanoseconds)
    }

    private fun median(samples: List<Timings>): Timings {
        return Timings(
            totalNanos = middle(samples.map { sample -> sample.totalNanos }),
            decodeNanos = middle(samples.map { sample -> sample.decodeNanos }),
            validateNanos = middle(samples.map { sample -> sample.validateNanos }),
            moduleNanos = middle(samples.map { sample -> sample.moduleNanos }),
            instantiateNanos = middle(samples.map { sample -> sample.instantiateNanos }),
            executeNanos = middle(samples.map { sample -> sample.executeNanos }),
        )
    }

    private fun middle(values: List<Long>): Long {

        val sorted = values.sorted()
        val upper = sorted[sorted.size / 2]

        return if (sorted.size % 2 == 1) {
            upper
        } else {
            (sorted[sorted.size / 2 - 1] + upper) / 2
        }
    }
}
