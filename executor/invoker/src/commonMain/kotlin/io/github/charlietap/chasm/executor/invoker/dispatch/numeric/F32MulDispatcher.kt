package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32MulDispatcher(
    instruction: NumericInstruction.F32Mul,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32MulExecutor(vstack, context, instruction)
    nextIp
}
