package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInitExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInit,
) = MemoryInitDispatcher(
    instruction = instruction,
    executor = ::MemoryInitExecutor,
)

internal inline fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInit,
    crossinline executor: Executor<MemoryInstruction.MemoryInit>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
