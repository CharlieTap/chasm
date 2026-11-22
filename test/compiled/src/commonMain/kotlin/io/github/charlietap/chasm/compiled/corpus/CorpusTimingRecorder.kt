package io.github.charlietap.chasm.compiled.corpus

import io.github.charlietap.corpus.lib.CorpusTimings
import kotlin.time.TimeSource

internal class CorpusTimingRecorder {
    var binarySizeBytes: Long = 0
    var moduleBuildCount: Int = 0

    private var decodeNanos: Long = 0
    private var validateNanos: Long = 0
    private var instantiateNanos: Long = 0
    var executeNanos: Long = 0
    var harnessNanos: Long = 0
    var executeDepth: Int = 0

    fun <T> decode(
        block: () -> T,
    ): T = measure(block) { elapsed -> decodeNanos += elapsed }

    fun <T> validate(
        block: () -> T,
    ): T = measure(block) { elapsed -> validateNanos += elapsed }

    fun <T> instantiate(
        block: () -> T,
    ): T = measure(block) { elapsed -> instantiateNanos += elapsed }

    inline fun <T> execute(
        block: () -> T,
    ): T {
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

    inline fun <T> harness(
        block: () -> T,
    ): T {
        if (executeDepth == 0) {
            return block()
        }

        return measure(block) { elapsed -> harnessNanos += elapsed }
    }

    fun snapshot(
        totalNanos: Long,
    ) = CorpusTimings(
        totalNanos = totalNanos,
        decodeNanos = decodeNanos,
        validateNanos = validateNanos,
        instantiateNanos = instantiateNanos,
        executeNanos = executeNanos - harnessNanos,
    )

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
