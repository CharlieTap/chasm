package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

internal typealias StringWriter = (LinearMemory, Int, String) -> Unit

internal expect inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
)
