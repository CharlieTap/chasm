package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I64Store16Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64Store16Dispatcher(
    instruction: FusedMemoryInstruction.I64Store16,
) = I64Store16Dispatcher(
    instruction = instruction,
    executor = ::I64Store16Executor,
)

internal inline fun I64Store16Dispatcher(
    instruction: FusedMemoryInstruction.I64Store16,
    crossinline executor: Executor<FusedMemoryInstruction.I64Store16>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
