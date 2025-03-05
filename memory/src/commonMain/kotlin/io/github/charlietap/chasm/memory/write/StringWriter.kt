package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias StringWriter = (LinearMemory, Int, String) -> Unit

expect inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
)
