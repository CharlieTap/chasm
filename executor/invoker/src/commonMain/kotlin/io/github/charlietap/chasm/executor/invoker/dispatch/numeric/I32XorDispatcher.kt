package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32XorExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32XorDispatcher(
    instruction: NumericInstruction.I32Xor,
) = I32XorDispatcher(
    instruction = instruction,
    executor = ::I32XorExecutor,
)

internal inline fun I32XorDispatcher(
    instruction: NumericInstruction.I32Xor,
    crossinline executor: Executor<NumericInstruction.I32Xor>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
