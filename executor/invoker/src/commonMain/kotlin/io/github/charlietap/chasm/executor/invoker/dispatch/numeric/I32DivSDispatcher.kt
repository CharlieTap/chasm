package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32DivSExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32DivSDispatcher(
    instruction: NumericInstruction.I32DivS,
) = I32DivSDispatcher(
    instruction = instruction,
    executor = ::I32DivSExecutor,
)

internal inline fun I32DivSDispatcher(
    instruction: NumericInstruction.I32DivS,
    crossinline executor: Executor<NumericInstruction.I32DivS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
