package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64DivSExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64DivSDispatcher(
    instruction: NumericInstruction.I64DivS,
) = I64DivSDispatcher(
    instruction = instruction,
    executor = ::I64DivSExecutor,
)

internal inline fun I64DivSDispatcher(
    instruction: NumericInstruction.I64DivS,
    crossinline executor: Executor<NumericInstruction.I64DivS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
