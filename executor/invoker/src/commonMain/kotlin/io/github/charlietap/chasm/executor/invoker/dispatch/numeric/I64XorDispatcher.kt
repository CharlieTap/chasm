package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64XorExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64XorDispatcher(
    instruction: NumericInstruction.I64Xor,
) = I64XorDispatcher(
    instruction = instruction,
    executor = ::I64XorExecutor,
)

internal inline fun I64XorDispatcher(
    instruction: NumericInstruction.I64Xor,
    crossinline executor: Executor<NumericInstruction.I64Xor>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
