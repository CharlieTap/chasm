package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64ShlExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64ShlDispatcher(
    instruction: NumericInstruction.I64Shl,
) = I64ShlDispatcher(
    instruction = instruction,
    executor = ::I64ShlExecutor,
)

internal inline fun I64ShlDispatcher(
    instruction: NumericInstruction.I64Shl,
    crossinline executor: Executor<NumericInstruction.I64Shl>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
