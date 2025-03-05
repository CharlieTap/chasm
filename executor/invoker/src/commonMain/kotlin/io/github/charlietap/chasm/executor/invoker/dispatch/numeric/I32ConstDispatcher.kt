package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.I32ConstExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ConstDispatcher(
    instruction: NumericInstruction.I32Const,
) = I32ConstDispatcher(
    instruction = instruction,
    executor = ::I32ConstExecutor,
)

internal inline fun I32ConstDispatcher(
    instruction: NumericInstruction.I32Const,
    crossinline executor: Executor<NumericInstruction.I32Const>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
