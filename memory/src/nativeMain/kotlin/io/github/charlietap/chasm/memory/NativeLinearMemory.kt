package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlinx.cinterop.CPointer

value class NativeLinearMemory(
    val pointer: CPointer<cnames.structs.LinearMemory>,
) : LinearMemory
