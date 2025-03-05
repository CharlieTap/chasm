package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32Load16UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32Load16UDispatcher(
    instruction: FusedMemoryInstruction.I32Load16U,
) = I32Load16UDispatcher(
    instruction = instruction,
    executor = ::I32Load16UExecutor,
)

internal inline fun I32Load16UDispatcher(
    instruction: FusedMemoryInstruction.I32Load16U,
    crossinline executor: Executor<FusedMemoryInstruction.I32Load16U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
