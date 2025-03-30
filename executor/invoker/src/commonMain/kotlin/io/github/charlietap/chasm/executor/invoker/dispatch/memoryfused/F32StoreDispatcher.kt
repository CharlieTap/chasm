package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.F32StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun F32StoreDispatcher(
    instruction: FusedMemoryInstruction.F32Store,
) = F32StoreDispatcher(
    instruction = instruction,
    executor = ::F32StoreExecutor,
)

internal inline fun F32StoreDispatcher(
    instruction: FusedMemoryInstruction.F32Store,
    crossinline executor: Executor<FusedMemoryInstruction.F32Store>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
