package io.github.charlietap.chasm.executor.memory.destruct

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryDestructor = (LinearMemory) -> Unit

expect fun LinearMemoryDestructor(
    memory: LinearMemory,
)
