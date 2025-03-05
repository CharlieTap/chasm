package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load8UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64Load8UDispatcher(
    instruction: FusedMemoryInstruction.I64Load8U,
) = I64Load8UDispatcher(
    instruction = instruction,
    executor = ::I64Load8UExecutor,
)

internal inline fun I64Load8UDispatcher(
    instruction: FusedMemoryInstruction.I64Load8U,
    crossinline executor: Executor<FusedMemoryInstruction.I64Load8U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
