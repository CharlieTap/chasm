package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

internal typealias NullTerminatedStringReader = (LinearMemory, Int) -> String

internal expect inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String
