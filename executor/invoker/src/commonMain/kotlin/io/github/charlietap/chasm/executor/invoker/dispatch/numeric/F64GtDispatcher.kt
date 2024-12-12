package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GtExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F64GtDispatcher(
    instruction: NumericInstruction.F64Gt,
) = F64GtDispatcher(
    instruction = instruction,
    executor = ::F64GtExecutor,
)

internal inline fun F64GtDispatcher(
    instruction: NumericInstruction.F64Gt,
    crossinline executor: Executor<NumericInstruction.F64Gt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
