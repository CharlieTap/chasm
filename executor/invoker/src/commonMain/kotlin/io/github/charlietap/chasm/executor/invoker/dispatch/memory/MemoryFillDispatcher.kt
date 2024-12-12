package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryFillExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFill,
) = MemoryFillDispatcher(
    instruction = instruction,
    executor = ::MemoryFillExecutor,
)

internal inline fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFill,
    crossinline executor: Executor<MemoryInstruction.MemoryFill>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
