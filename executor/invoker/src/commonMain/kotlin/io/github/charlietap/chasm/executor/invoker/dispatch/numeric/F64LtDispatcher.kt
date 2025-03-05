package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64LtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64LtDispatcher(
    instruction: NumericInstruction.F64Lt,
) = F64LtDispatcher(
    instruction = instruction,
    executor = ::F64LtExecutor,
)

internal inline fun F64LtDispatcher(
    instruction: NumericInstruction.F64Lt,
    crossinline executor: Executor<NumericInstruction.F64Lt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
