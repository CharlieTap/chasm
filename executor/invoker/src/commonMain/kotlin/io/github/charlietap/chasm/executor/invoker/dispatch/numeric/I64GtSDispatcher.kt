package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64GtSDispatcher(
    instruction: NumericInstruction.I64GtS,
) = I64GtSDispatcher(
    instruction = instruction,
    executor = ::I64GtSExecutor,
)

internal inline fun I64GtSDispatcher(
    instruction: NumericInstruction.I64GtS,
    crossinline executor: Executor<NumericInstruction.I64GtS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
