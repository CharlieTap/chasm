package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.F32ConstExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConstDispatcher(
    instruction: NumericInstruction.F32Const,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32ConstExecutor(vstack, context, instruction)
    nextIp
}
