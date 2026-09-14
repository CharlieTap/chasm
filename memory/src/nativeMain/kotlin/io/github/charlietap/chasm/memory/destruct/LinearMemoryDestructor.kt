package io.github.charlietap.chasm.memory.destruct

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual fun LinearMemoryDestructor(
    memory: LinearMemory,
) {
    (memory as? NativeMappedLinearMemory)?.release()
}
