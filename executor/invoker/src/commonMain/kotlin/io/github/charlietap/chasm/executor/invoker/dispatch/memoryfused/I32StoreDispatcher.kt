package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I32StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32StoreDispatcher(
    instruction: FusedMemoryInstruction.I32Store,
) = I32StoreDispatcher(
    instruction = instruction,
    executor = ::I32StoreExecutor,
)

internal inline fun I32StoreDispatcher(
    instruction: FusedMemoryInstruction.I32Store,
    crossinline executor: Executor<FusedMemoryInstruction.I32Store>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
