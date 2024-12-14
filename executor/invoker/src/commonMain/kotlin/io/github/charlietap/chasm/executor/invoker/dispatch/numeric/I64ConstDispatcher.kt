package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.I64ConstExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64ConstDispatcher(
    instruction: NumericInstruction.I64Const,
) = I64ConstDispatcher(
    instruction = instruction,
    executor = ::I64ConstExecutor,
)

internal inline fun I64ConstDispatcher(
    instruction: NumericInstruction.I64Const,
    crossinline executor: Executor<NumericInstruction.I64Const>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
