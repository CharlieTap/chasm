package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64GeDispatcher(
    instruction: NumericInstruction.F64Ge,
) = F64GeDispatcher(
    instruction = instruction,
    executor = ::F64GeExecutor,
)

internal inline fun F64GeDispatcher(
    instruction: NumericInstruction.F64Ge,
    crossinline executor: Executor<NumericInstruction.F64Ge>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
