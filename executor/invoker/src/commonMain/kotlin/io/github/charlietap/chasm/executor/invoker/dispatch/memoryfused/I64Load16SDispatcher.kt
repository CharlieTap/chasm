package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64Load16SDispatcher(
    instruction: FusedMemoryInstruction.I64Load16S,
) = I64Load16SDispatcher(
    instruction = instruction,
    executor = ::I64Load16SExecutor,
)

internal inline fun I64Load16SDispatcher(
    instruction: FusedMemoryInstruction.I64Load16S,
    crossinline executor: Executor<FusedMemoryInstruction.I64Load16S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
