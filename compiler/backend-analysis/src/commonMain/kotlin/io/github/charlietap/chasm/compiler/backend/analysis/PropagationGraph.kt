package io.github.charlietap.chasm.compiler.backend.analysis

internal class PropagationGraph(
    nodes: Int,
    sources: IntArray,
    destinations: IntArray,
) {
    private val first = IntArray(nodes + 1)
    private val reaches = IntArray(sources.size)

    init {
        for (source in sources) first[source + 1]++
        for (node in 0 until nodes) first[node + 1] += first[node]
        val next = first.copyOf(nodes)
        for (edge in sources.indices) reaches[next[sources[edge]]++] = destinations[edge]
    }

    fun spread(
        marked: BooleanArray,
        blocked: BooleanArray? = null,
    ) {
        val pending = IntArray(marked.size)
        var count = 0
        for (node in marked.indices) if (marked[node]) pending[count++] = node
        while (count > 0) {
            val node = pending[--count]
            for (edge in first[node] until first[node + 1]) {
                val destination = reaches[edge]
                if (!marked[destination] && (blocked == null || !blocked[destination])) {
                    marked[destination] = true
                    pending[count++] = destination
                }
            }
        }
    }
}
