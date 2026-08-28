package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RotlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64RotlDispatcher(
    instruction: NumericInstruction.I64Rotl,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64RotlExecutor(vstack, context, instruction)
    nextIp
}
