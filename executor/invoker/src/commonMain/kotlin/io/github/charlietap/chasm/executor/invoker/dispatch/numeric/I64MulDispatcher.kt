package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64MulDispatcher(
    instruction: NumericInstruction.I64Mul,
) = I64MulDispatcher(
    instruction = instruction,
    executor = ::I64MulExecutor,
)

internal inline fun I64MulDispatcher(
    instruction: NumericInstruction.I64Mul,
    crossinline executor: Executor<NumericInstruction.I64Mul>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
