package io.github.charlietap.chasm.executor.memory.factory

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.alloc

actual fun LinearMemoryFactory(pages: LinearMemory.Pages): LinearMemory {
    val pointer = alloc(pages.amount)
    return NativeLinearMemory(pointer!!)
}
