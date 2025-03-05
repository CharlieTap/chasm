package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.F32ConstExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConstDispatcher(
    instruction: NumericInstruction.F32Const,
) = F32ConstDispatcher(
    instruction = instruction,
    executor = ::F32ConstExecutor,
)

internal inline fun F32ConstDispatcher(
    instruction: NumericInstruction.F32Const,
    crossinline executor: Executor<NumericInstruction.F32Const>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
