package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64MulDispatcher(
    instruction: NumericInstruction.F64Mul,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64MulExecutor(vstack, context, instruction)
    nextIp
}
