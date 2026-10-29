package io.github.charlietap.chasm.compiler.backend.analysis

internal class CopyGraph(
    slots: Int,
    sources: IntArray,
    destinations: IntArray,
) {
    private val first = IntArray(slots + 1)
    private val copiedTo = IntArray(sources.size)

    init {
        for (source in sources) first[source + 1]++
        for (slot in 0 until slots) first[slot + 1] += first[slot]
        val next = first.copyOf(slots)
        for (edge in sources.indices) copiedTo[next[sources[edge]]++] = destinations[edge]
    }

    fun spread(
        marked: BooleanArray,
        blocked: BooleanArray? = null,
    ) {
        val pending = IntArray(marked.size)
        var count = 0
        for (slot in marked.indices) if (marked[slot]) pending[count++] = slot
        while (count > 0) {
            val slot = pending[--count]
            for (edge in first[slot] until first[slot + 1]) {
                val destination = copiedTo[edge]
                if (!marked[destination] && (blocked == null || !blocked[destination])) {
                    marked[destination] = true
                    pending[count++] = destination
                }
            }
        }
    }
}
