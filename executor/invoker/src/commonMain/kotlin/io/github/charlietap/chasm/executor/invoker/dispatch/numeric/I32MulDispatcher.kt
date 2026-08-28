package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32MulDispatcher(
    instruction: NumericInstruction.I32Mul,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32MulExecutor(vstack, context, instruction)
    nextIp
}
