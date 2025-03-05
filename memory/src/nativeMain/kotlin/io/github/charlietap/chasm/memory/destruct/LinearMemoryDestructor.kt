package io.github.charlietap.chasm.memory.destruct

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.dealloc

actual fun LinearMemoryDestructor(
    memory: LinearMemory,
) {
    val memory = (memory as NativeLinearMemory)
    dealloc(memory.pointer)
}
