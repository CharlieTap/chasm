package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32MulDispatcher(
    instruction: FusedNumericInstruction.I32Mul,
) = I32MulDispatcher(
    instruction = instruction,
    executor = ::I32MulExecutor,
)

internal inline fun I32MulDispatcher(
    instruction: FusedNumericInstruction.I32Mul,
    crossinline executor: Executor<FusedNumericInstruction.I32Mul>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
