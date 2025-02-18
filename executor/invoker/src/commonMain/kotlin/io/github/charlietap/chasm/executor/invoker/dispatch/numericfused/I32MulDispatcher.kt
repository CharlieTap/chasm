package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32MulExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

fun I32MulDispatcher(
    instruction: FusedNumericInstruction.I32Mul,
) = I32MulDispatcher(
    instruction = instruction,
    executor = ::I32MulExecutor,
)

internal inline fun I32MulDispatcher(
    instruction: FusedNumericInstruction.I32Mul,
    crossinline executor: Executor<FusedNumericInstruction.I32Mul>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
