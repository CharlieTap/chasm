package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun memoryRuntimeInstruction(): MemoryInstruction = dataDropRuntimeInstruction()

fun dataDropRuntimeInstruction(
    data: DataInstance = dataInstance(),
) = MemoryInstruction.DataDrop(data)
