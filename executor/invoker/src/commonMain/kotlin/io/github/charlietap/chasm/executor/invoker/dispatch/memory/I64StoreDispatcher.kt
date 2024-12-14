package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I64StoreDispatcher(
    instruction: MemoryInstruction.I64Store,
) = I64StoreDispatcher(
    instruction = instruction,
    executor = ::I64StoreExecutor,
)

internal inline fun I64StoreDispatcher(
    instruction: MemoryInstruction.I64Store,
    crossinline executor: Executor<MemoryInstruction.I64Store>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}