package io.github.charlietap.chasm.compiler.backend.dex.lower

internal class Chunk(
    val start: Int,
    val end: Int,
    val entries: IntArray,
    private val continuations: IntArray,
    private val next: Int,
    val spills: BooleanArray? = null,
    val constants: BooleanArray? = null,
    val usesMemory: Boolean = true,
    val protects: Boolean = true,
    val reloads: BooleanArray? = null,
    val mixed: IntArray = NO_SLOTS,
) {
    val exits = LinkedHashSet<Int>()

    fun continuation(target: Int): Int {

        val id = continuations[target]
        if (id >= 0) {
            return id
        }

        check(target == end) { "no continuation for $target" }
        return next
    }

    var leavesAnywhere = false

    operator fun contains(
        position: Int,
    ): Boolean = position in start until end

    companion object {
        const val RETURN = -1

        const val LEAVE_WITHOUT_RESULT = -2

        private val NO_SLOTS = IntArray(0)
    }
}
