package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun DataDropExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.DataDrop,
) {
    instruction.data.bytes = DataInstance.EMPTY
}
