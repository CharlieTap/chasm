package io.github.charlietap.chasm.executor.invoker.dispatch.numeric_fused

import io.github.charlietap.chasm.executor.invoker.instruction.numeric_fused.unop.F32AbsExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

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
