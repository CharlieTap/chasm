package io.github.charlietap.chasm.compiler.backend.jvm.lower

import org.objectweb.asm.Label

internal class Chunk(
    val start: Int,
    val end: Int,
    val entries: IntArray,
    val reloads: BooleanArray? = null,
    val spills: BooleanArray? = null,
    val continuationIds: IntArray? = null,
) {
    val exits = LinkedHashMap<Int, Label>()

    val dynamicExits = LinkedHashSet<Int>()

    val spill = Label()

    val endMarker = Label()

    var leavesAnywhere = false

    operator fun contains(
        position: Int,
    ): Boolean = position in start until end

    fun exit(
        position: Int,
    ): Label = exits.getOrPut(position) { Label() }

    fun continuation(
        position: Int,
    ): Int = continuationIds?.get(position) ?: 0

    companion object {
        const val RETURN = -1

        const val LEAVE_WITHOUT_RESULT = -2

        const val DYNAMIC_EXIT_BASE = -3
    }
}
