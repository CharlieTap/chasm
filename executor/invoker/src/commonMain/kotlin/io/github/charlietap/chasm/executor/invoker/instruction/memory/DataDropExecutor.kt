package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

internal inline fun DataDropExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.DataDrop,
) {
    instruction.data.bytes = DataInstance.EMPTY
}
