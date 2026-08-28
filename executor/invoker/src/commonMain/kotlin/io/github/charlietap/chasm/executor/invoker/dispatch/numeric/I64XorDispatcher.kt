package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64XorExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64XorDispatcher(
    instruction: NumericInstruction.I64Xor,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64XorExecutor(vstack, context, instruction)
    nextIp
}
