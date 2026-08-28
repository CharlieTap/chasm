package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.I32ConstExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ConstDispatcher(
    instruction: NumericInstruction.I32Const,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32ConstExecutor(vstack, context, instruction)
    nextIp
}
