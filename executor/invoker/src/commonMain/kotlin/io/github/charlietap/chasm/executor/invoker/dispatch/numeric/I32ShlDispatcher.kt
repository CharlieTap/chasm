package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShlExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32ShlDispatcher(
    instruction: NumericInstruction.I32Shl,
) = I32ShlDispatcher(
    instruction = instruction,
    executor = ::I32ShlExecutor,
)

internal inline fun I32ShlDispatcher(
    instruction: NumericInstruction.I32Shl,
    crossinline executor: Executor<NumericInstruction.I32Shl>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
