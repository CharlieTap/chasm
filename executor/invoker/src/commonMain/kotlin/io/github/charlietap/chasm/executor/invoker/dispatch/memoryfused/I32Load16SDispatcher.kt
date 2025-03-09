package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32Load16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32Load16SDispatcher(
    instruction: FusedMemoryInstruction.I32Load16S,
) = I32Load16SDispatcher(
    instruction = instruction,
    executor = ::I32Load16SExecutor,
)

internal inline fun I32Load16SDispatcher(
    instruction: FusedMemoryInstruction.I32Load16S,
    crossinline executor: Executor<FusedMemoryInstruction.I32Load16S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
