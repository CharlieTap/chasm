package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32XorExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32XorDispatcher(
    instruction: NumericInstruction.I32Xor,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32XorExecutor(vstack, context, instruction)
    nextIp
}
