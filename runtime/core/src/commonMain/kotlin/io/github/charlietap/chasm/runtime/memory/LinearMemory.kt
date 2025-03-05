package io.github.charlietap.chasm.runtime.memory

import kotlin.jvm.JvmInline

interface LinearMemory {

    @JvmInline
    value class Pages(val amount: UInt)

    companion object {
        const val PAGE_SIZE = 65536
        const val MAX_PAGES = PAGE_SIZE
    }
}
