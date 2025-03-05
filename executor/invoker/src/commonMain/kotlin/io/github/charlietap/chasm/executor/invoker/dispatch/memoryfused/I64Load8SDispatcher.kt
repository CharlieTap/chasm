package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64Load8SDispatcher(
    instruction: FusedMemoryInstruction.I64Load8S,
) = I64Load8SDispatcher(
    instruction = instruction,
    executor = ::I64Load8SExecutor,
)

internal inline fun I64Load8SDispatcher(
    instruction: FusedMemoryInstruction.I64Load8S,
    crossinline executor: Executor<FusedMemoryInstruction.I64Load8S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
