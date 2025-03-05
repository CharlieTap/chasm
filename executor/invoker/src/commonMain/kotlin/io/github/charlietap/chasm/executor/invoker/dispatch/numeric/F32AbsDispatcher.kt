package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32AbsExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32AbsDispatcher(
    instruction: NumericInstruction.F32Abs,
) = F32AbsDispatcher(
    instruction = instruction,
    executor = ::F32AbsExecutor,
)

internal inline fun F32AbsDispatcher(
    instruction: NumericInstruction.F32Abs,
    crossinline executor: Executor<NumericInstruction.F32Abs>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
