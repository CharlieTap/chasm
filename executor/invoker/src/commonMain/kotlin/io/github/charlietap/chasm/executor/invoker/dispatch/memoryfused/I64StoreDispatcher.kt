package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I64StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64StoreDispatcher(
    instruction: FusedMemoryInstruction.I64Store,
) = I64StoreDispatcher(
    instruction = instruction,
    executor = ::I64StoreExecutor,
)

internal inline fun I64StoreDispatcher(
    instruction: FusedMemoryInstruction.I64Store,
    crossinline executor: Executor<FusedMemoryInstruction.I64Store>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
