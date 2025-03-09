package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.F32LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun F32LoadDispatcher(
    instruction: FusedMemoryInstruction.F32Load,
) = F32LoadDispatcher(
    instruction = instruction,
    executor = ::F32LoadExecutor,
)

internal inline fun F32LoadDispatcher(
    instruction: FusedMemoryInstruction.F32Load,
    crossinline executor: Executor<FusedMemoryInstruction.F32Load>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
