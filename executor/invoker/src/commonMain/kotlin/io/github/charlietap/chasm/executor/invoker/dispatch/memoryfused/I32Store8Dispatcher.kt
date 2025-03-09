package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I32Store8Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32Store8Dispatcher(
    instruction: FusedMemoryInstruction.I32Store8,
) = I32Store8Dispatcher(
    instruction = instruction,
    executor = ::I32Store8Executor,
)

internal inline fun I32Store8Dispatcher(
    instruction: FusedMemoryInstruction.I32Store8,
    crossinline executor: Executor<FusedMemoryInstruction.I32Store8>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
