package io.github.charlietap.chasm.executor.runtime.memory

import kotlin.jvm.JvmInline

interface LinearMemory {

    @JvmInline
    value class Pages(val amount: Int)

    val min: Pages
    val max: Pages?

    fun size() = min.amount.toLong() * PAGE_SIZE

    companion object {
        const val PAGE_SIZE = 65536
        const val MAX_PAGES = PAGE_SIZE
    }
}
