package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LtSExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32LtSDispatcher(
    instruction: NumericInstruction.I32LtS,
) = I32LtSDispatcher(
    instruction = instruction,
    executor = ::I32LtSExecutor,
)

internal inline fun I32LtSDispatcher(
    instruction: NumericInstruction.I32LtS,
    crossinline executor: Executor<NumericInstruction.I32LtS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
