package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32AbsExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32AbsDispatcher(
    instruction: FusedNumericInstruction.F32Abs,
) = F32AbsDispatcher(
    instruction = instruction,
    executor = ::F32AbsExecutor,
)

internal inline fun F32AbsDispatcher(
    instruction: FusedNumericInstruction.F32Abs,
    crossinline executor: Executor<FusedNumericInstruction.F32Abs>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
