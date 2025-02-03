package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias StringWriter = (LinearMemory, Int, String) -> Unit

expect inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
)
