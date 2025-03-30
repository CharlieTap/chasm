package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I32Store16Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32Store16Dispatcher(
    instruction: FusedMemoryInstruction.I32Store16,
) = I32Store16Dispatcher(
    instruction = instruction,
    executor = ::I32Store16Executor,
)

internal inline fun I32Store16Dispatcher(
    instruction: FusedMemoryInstruction.I32Store16,
    crossinline executor: Executor<FusedMemoryInstruction.I32Store16>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
