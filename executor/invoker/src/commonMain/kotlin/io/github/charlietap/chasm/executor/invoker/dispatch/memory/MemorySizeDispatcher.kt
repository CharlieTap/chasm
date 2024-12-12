package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemorySizeExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun MemorySizeDispatcher(
    instruction: MemoryInstruction.MemorySize,
) = MemorySizeDispatcher(
    instruction = instruction,
    executor = ::MemorySizeExecutor,
)

internal inline fun MemorySizeDispatcher(
    instruction: MemoryInstruction.MemorySize,
    crossinline executor: Executor<MemoryInstruction.MemorySize>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
