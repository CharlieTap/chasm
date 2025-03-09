package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32Load8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32Load8SDispatcher(
    instruction: FusedMemoryInstruction.I32Load8S,
) = I32Load8SDispatcher(
    instruction = instruction,
    executor = ::I32Load8SExecutor,
)

internal inline fun I32Load8SDispatcher(
    instruction: FusedMemoryInstruction.I32Load8S,
    crossinline executor: Executor<FusedMemoryInstruction.I32Load8S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
