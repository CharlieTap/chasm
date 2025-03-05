package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias NullTerminatedStringReader = (LinearMemory, Int) -> String

expect inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String
