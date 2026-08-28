package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32RotlDispatcher(
    instruction: NumericInstruction.I32Rotl,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32RotlExecutor(vstack, context, instruction)
    nextIp
}
