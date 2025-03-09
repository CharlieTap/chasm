package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.F64StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun F64StoreDispatcher(
    instruction: FusedMemoryInstruction.F64Store,
) = F64StoreDispatcher(
    instruction = instruction,
    executor = ::F64StoreExecutor,
)

internal inline fun F64StoreDispatcher(
    instruction: FusedMemoryInstruction.F64Store,
    crossinline executor: Executor<FusedMemoryInstruction.F64Store>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
