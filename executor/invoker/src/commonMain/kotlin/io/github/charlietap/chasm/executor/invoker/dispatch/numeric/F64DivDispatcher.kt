package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64DivExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64DivDispatcher(
    instruction: NumericInstruction.F64Div,
) = F64DivDispatcher(
    instruction = instruction,
    executor = ::F64DivExecutor,
)

internal inline fun F64DivDispatcher(
    instruction: NumericInstruction.F64Div,
    crossinline executor: Executor<NumericInstruction.F64Div>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
