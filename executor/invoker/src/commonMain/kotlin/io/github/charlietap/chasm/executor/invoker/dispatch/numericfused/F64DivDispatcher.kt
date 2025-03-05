package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64DivExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64DivDispatcher(
    instruction: FusedNumericInstruction.F64Div,
) = F64DivDispatcher(
    instruction = instruction,
    executor = ::F64DivExecutor,
)

internal inline fun F64DivDispatcher(
    instruction: FusedNumericInstruction.F64Div,
    crossinline executor: Executor<FusedNumericInstruction.F64Div>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
