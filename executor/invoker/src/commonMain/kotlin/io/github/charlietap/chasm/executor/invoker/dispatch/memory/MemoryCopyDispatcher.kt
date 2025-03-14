package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryCopyExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopy,
) = MemoryCopyDispatcher(
    instruction = instruction,
    executor = ::MemoryCopyExecutor,
)

internal inline fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopy,
    crossinline executor: Executor<MemoryInstruction.MemoryCopy>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
