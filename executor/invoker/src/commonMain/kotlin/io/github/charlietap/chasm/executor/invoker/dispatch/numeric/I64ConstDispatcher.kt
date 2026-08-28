package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.I64ConstExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ConstDispatcher(
    instruction: NumericInstruction.I64Const,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64ConstExecutor(vstack, context, instruction)
    nextIp
}
