package io.github.charlietap.chasm.compiler.program

/**
 * Fused steps cover several source instructions, so a range's end is only
 * known once its step completes. Instructions emitted outside a step have no
 * source.
 */
internal class SourceProvenanceRecorder {

    private var starts = IntArray(INITIAL_CAPACITY)
    private var ends = IntArray(INITIAL_CAPACITY)
    private var currentStart = NO_SOURCE
    private var stepFirstOffset = 0

    var size = 0
        private set

    fun beginStep(sourceIndex: Int) {
        currentStart = sourceIndex
        stepFirstOffset = size
    }

    fun endStep(sourceEndExclusive: Int) {
        for (offset in stepFirstOffset until size) {
            ends[offset] = sourceEndExclusive
        }
        currentStart = NO_SOURCE
        stepFirstOffset = size
    }

    fun record(offset: Int) {
        if (offset >= starts.size) {
            val capacity = maxOf(starts.size * 2, offset + 1)
            starts = starts.copyOf(capacity)
            ends = ends.copyOf(capacity)
        }
        starts[offset] = currentStart
        ends[offset] = if (currentStart == NO_SOURCE) NO_SOURCE else currentStart + 1
        if (offset >= size) size = offset + 1
    }

    fun sourceStarts(): IntArray = starts.copyOf(size)

    fun sourceEnds(): IntArray = ends.copyOf(size)

    companion object {
        const val NO_SOURCE = -1
        private const val INITIAL_CAPACITY = 64
    }
}
