package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F32StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun F32StoreDispatcher(
    instruction: MemoryInstruction.F32Store,
) = F32StoreDispatcher(
    instruction = instruction,
    executor = ::F32StoreExecutor,
)

internal inline fun F32StoreDispatcher(
    instruction: MemoryInstruction.F32Store,
    crossinline executor: Executor<MemoryInstruction.F32Store>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
