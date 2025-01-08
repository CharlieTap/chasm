package io.github.charlietap.chasm.executor.memory.destruct

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.dealloc

actual fun LinearMemoryDestructor(
    memory: LinearMemory,
) {
    val memory = (memory as NativeLinearMemory)
    dealloc(memory.pointer)
}
