package io.github.charlietap.chasm.compiler.backend.dex.lower

internal class Chunk(
    val start: Int,
    val end: Int,
    val entries: IntArray,
    val spills: BooleanArray? = null,
    val constants: BooleanArray? = null,
    val usesMemory: Boolean = true,
    val protects: Boolean = true,
    val reloads: BooleanArray? = null,
) {
    val exits = LinkedHashSet<Int>()

    var leavesAnywhere = false

    operator fun contains(
        position: Int,
    ): Boolean = position in start until end

    companion object {
        const val RETURN = -1

        const val LEAVE_WITHOUT_RESULT = -2
    }
}
