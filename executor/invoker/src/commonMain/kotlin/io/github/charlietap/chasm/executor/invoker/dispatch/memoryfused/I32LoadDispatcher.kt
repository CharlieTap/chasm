package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32LoadDispatcher(
    instruction: FusedMemoryInstruction.I32Load,
) = I32LoadDispatcher(
    instruction = instruction,
    executor = ::I32LoadExecutor,
)

internal inline fun I32LoadDispatcher(
    instruction: FusedMemoryInstruction.I32Load,
    crossinline executor: Executor<FusedMemoryInstruction.I32Load>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
