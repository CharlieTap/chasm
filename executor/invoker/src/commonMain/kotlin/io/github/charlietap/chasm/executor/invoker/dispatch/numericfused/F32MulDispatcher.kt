package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32MulDispatcher(
    instruction: FusedNumericInstruction.F32Mul,
) = F32MulDispatcher(
    instruction = instruction,
    executor = ::F32MulExecutor,
)

internal inline fun F32MulDispatcher(
    instruction: FusedNumericInstruction.F32Mul,
    crossinline executor: Executor<FusedNumericInstruction.F32Mul>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
