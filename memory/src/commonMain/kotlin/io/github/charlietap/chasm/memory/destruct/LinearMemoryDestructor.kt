package io.github.charlietap.chasm.memory.destruct

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias LinearMemoryDestructor = (LinearMemory) -> Unit

@InternalChasmApi
expect fun LinearMemoryDestructor(
    memory: LinearMemory,
)
