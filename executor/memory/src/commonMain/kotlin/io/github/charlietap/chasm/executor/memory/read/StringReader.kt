package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias StringReader = (LinearMemory, Int, Int) -> String

expect inline fun StringReader(
    memory: LinearMemory,
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String
