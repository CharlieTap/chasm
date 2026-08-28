package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.F64ConstExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConstDispatcher(
    instruction: NumericInstruction.F64Const,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64ConstExecutor(vstack, context, instruction)
    nextIp
}
