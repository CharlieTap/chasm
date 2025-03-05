package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32Load8UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32Load8UDispatcher(
    instruction: FusedMemoryInstruction.I32Load8U,
) = I32Load8UDispatcher(
    instruction = instruction,
    executor = ::I32Load8UExecutor,
)

internal inline fun I32Load8UDispatcher(
    instruction: FusedMemoryInstruction.I32Load8U,
    crossinline executor: Executor<FusedMemoryInstruction.I32Load8U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
