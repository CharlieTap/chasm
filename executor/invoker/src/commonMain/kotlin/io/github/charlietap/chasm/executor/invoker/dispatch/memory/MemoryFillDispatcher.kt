package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryFillExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFill,
) = MemoryFillDispatcher(
    instruction = instruction,
    executor = ::MemoryFillExecutor,
)

internal inline fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFill,
    crossinline executor: Executor<MemoryInstruction.MemoryFill>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
