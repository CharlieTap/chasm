package io.github.charlietap.chasm.runtime.execution

import kotlin.concurrent.Volatile

object CompiledStack {

    const val UNIT_BYTES = 32L

    @Volatile
    var budgetSource: () -> Int = { budgetFor(1L shl 20) }

    val budget: Int get() = budgetSource()

    fun budgetFor(
        stackBytes: Long,
    ): Int = (stackBytes / 4 * 3 / UNIT_BYTES).coerceIn(0L, Int.MAX_VALUE / 2L).toInt()
}
