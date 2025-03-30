package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32RotlDispatcher(
    instruction: NumericInstruction.I32Rotl,
) = I32RotlDispatcher(
    instruction = instruction,
    executor = ::I32RotlExecutor,
)

internal inline fun I32RotlDispatcher(
    instruction: NumericInstruction.I32Rotl,
    crossinline executor: Executor<NumericInstruction.I32Rotl>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
