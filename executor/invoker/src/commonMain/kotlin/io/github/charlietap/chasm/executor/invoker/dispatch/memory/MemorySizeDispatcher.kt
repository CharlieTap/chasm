package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemorySizeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemorySizeDispatcher(
    instruction: MemoryInstruction.MemorySize,
) = MemorySizeDispatcher(
    instruction = instruction,
    executor = ::MemorySizeExecutor,
)

internal inline fun MemorySizeDispatcher(
    instruction: MemoryInstruction.MemorySize,
    crossinline executor: Executor<MemoryInstruction.MemorySize>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
