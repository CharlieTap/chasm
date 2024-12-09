package io.github.charlietap.chasm.executor.memory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import kotlinx.cinterop.CPointer

value class NativeLinearMemory(
    val pointer: CPointer<cnames.structs.LinearMemory>,
) : LinearMemory
