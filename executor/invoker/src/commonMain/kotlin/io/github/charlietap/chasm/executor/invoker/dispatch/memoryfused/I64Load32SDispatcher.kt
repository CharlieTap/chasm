package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load32SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction

fun I64Load32SDispatcher(
    instruction: FusedMemoryInstruction.I64Load32S,
) = I64Load32SDispatcher(
    instruction = instruction,
    executor = ::I64Load32SExecutor,
)

internal inline fun I64Load32SDispatcher(
    instruction: FusedMemoryInstruction.I64Load32S,
    crossinline executor: Executor<FusedMemoryInstruction.I64Load32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
