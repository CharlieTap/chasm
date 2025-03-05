package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryGrowExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryGrowDispatcher(
    instruction: MemoryInstruction.MemoryGrow,
) = MemoryGrowDispatcher(
    instruction = instruction,
    executor = ::MemoryGrowExecutor,
)

internal inline fun MemoryGrowDispatcher(
    instruction: MemoryInstruction.MemoryGrow,
    crossinline executor: Executor<MemoryInstruction.MemoryGrow>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
