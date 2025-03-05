package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32OrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32OrDispatcher(
    instruction: NumericInstruction.I32Or,
) = I32OrDispatcher(
    instruction = instruction,
    executor = ::I32OrExecutor,
)

internal inline fun I32OrDispatcher(
    instruction: NumericInstruction.I32Or,
    crossinline executor: Executor<NumericInstruction.I32Or>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
