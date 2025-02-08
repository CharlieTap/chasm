package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias NullTerminatedStringReader = (LinearMemory, Int) -> String

expect inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String
