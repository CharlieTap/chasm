package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32StoreDispatcher(
    instruction: MemoryInstruction.I32Store,
) = I32StoreDispatcher(
    instruction = instruction,
    executor = ::I32StoreExecutor,
)

internal inline fun I32StoreDispatcher(
    instruction: MemoryInstruction.I32Store,
    crossinline executor: Executor<MemoryInstruction.I32Store>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
