package io.github.charlietap.chasm.memory.destruct

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias LinearMemoryDestructor = (LinearMemory) -> Unit

expect fun LinearMemoryDestructor(
    memory: LinearMemory,
)
