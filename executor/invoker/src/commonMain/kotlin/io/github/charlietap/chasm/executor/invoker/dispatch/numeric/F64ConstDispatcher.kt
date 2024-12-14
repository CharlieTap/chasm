package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.F64ConstExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F64ConstDispatcher(
    instruction: NumericInstruction.F64Const,
) = F64ConstDispatcher(
    instruction = instruction,
    executor = ::F64ConstExecutor,
)

internal inline fun F64ConstDispatcher(
    instruction: NumericInstruction.F64Const,
    crossinline executor: Executor<NumericInstruction.F64Const>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
