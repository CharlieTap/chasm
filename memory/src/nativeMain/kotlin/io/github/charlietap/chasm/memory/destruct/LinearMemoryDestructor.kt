package io.github.charlietap.chasm.memory.destruct

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual fun LinearMemoryDestructor(
    memory: LinearMemory,
) {
    (memory as? NativeMappedLinearMemory)?.release()
}
