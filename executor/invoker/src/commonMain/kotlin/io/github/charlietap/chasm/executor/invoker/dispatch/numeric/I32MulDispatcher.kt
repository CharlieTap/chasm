package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32MulDispatcher(
    instruction: NumericInstruction.I32Mul,
) = I32MulDispatcher(
    instruction = instruction,
    executor = ::I32MulExecutor,
)

internal inline fun I32MulDispatcher(
    instruction: NumericInstruction.I32Mul,
    crossinline executor: Executor<NumericInstruction.I32Mul>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
