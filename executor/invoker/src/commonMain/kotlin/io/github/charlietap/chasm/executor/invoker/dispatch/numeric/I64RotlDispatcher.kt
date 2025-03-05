package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RotlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64RotlDispatcher(
    instruction: NumericInstruction.I64Rotl,
) = I64RotlDispatcher(
    instruction = instruction,
    executor = ::I64RotlExecutor,
)

internal inline fun I64RotlDispatcher(
    instruction: NumericInstruction.I64Rotl,
    crossinline executor: Executor<NumericInstruction.I64Rotl>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
