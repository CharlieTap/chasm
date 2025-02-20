package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I64Store8Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction

fun I64Store8Dispatcher(
    instruction: FusedMemoryInstruction.I64Store8,
) = I64Store8Dispatcher(
    instruction = instruction,
    executor = ::I64Store8Executor,
)

internal inline fun I64Store8Dispatcher(
    instruction: FusedMemoryInstruction.I64Store8,
    crossinline executor: Executor<FusedMemoryInstruction.I64Store8>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
