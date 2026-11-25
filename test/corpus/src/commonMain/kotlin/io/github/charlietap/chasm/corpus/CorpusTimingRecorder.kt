package io.github.charlietap.chasm.corpus

import io.github.charlietap.corpus.lib.measurement.Timings
import kotlin.time.TimeSource

internal class CorpusTimingRecorder {
    var moduleBuildCount: Int = 0

    private var decodeNanos: Long = 0
    private var validateNanos: Long = 0
    private var moduleNanos: Long = 0
    private var instantiateNanos: Long = 0

    @PublishedApi
    internal var executeNanos: Long = 0

    @PublishedApi
    internal var harnessNanos: Long = 0
        private set

    @PublishedApi
    internal var executeDepth: Int = 0
        private set

    fun <T> decode(block: () -> T): T {
        return measure(block) { elapsed -> decodeNanos += elapsed }
    }

    fun <T> validate(block: () -> T): T {
        return measure(block) { elapsed -> validateNanos += elapsed }
    }

    fun <T> module(block: () -> T): T {
        return measure(block) { elapsed -> moduleNanos += elapsed }
    }

    fun <T> instantiate(block: () -> T): T {
        return measure(block) { elapsed -> instantiateNanos += elapsed }
    }

    inline fun <T> execute(block: () -> T): T {

        if (executeDepth > 0) {
            return block()
        }

        executeDepth++
        return try {
            measure(block) { elapsed -> executeNanos += elapsed }
        } finally {
            executeDepth--
        }
    }

    inline fun <T> harness(block: () -> T): T {

        if (executeDepth == 0) {
            return block()
        }

        return measure(block) { elapsed -> harnessNanos += elapsed }
    }

    fun timings(totalNanos: Long): Timings {
        return Timings(
            totalNanos = totalNanos,
            decodeNanos = decodeNanos,
            validateNanos = validateNanos,
            moduleNanos = moduleNanos,
            instantiateNanos = instantiateNanos,
            executeNanos = executeNanos - harnessNanos,
        )
    }

    inline fun <T> measure(
        block: () -> T,
        record: (Long) -> Unit,
    ): T {

        val start = TimeSource.Monotonic.markNow()

        return try {
            block()
        } finally {
            record(start.elapsedNow().inWholeNanoseconds)
        }
    }
}
