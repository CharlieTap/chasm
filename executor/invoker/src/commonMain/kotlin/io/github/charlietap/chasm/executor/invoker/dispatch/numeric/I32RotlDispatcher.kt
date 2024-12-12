package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotlExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32RotlDispatcher(
    instruction: NumericInstruction.I32Rotl,
) = I32RotlDispatcher(
    instruction = instruction,
    executor = ::I32RotlExecutor,
)

internal inline fun I32RotlDispatcher(
    instruction: NumericInstruction.I32Rotl,
    crossinline executor: Executor<NumericInstruction.I32Rotl>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
