package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64MulDispatcher(
    instruction: NumericInstruction.I64Mul,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64MulExecutor(vstack, context, instruction)
    nextIp
}
