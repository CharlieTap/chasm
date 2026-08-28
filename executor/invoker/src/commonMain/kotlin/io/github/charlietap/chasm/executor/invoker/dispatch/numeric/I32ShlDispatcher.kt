package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ShlDispatcher(
    instruction: NumericInstruction.I32Shl,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32ShlExecutor(vstack, context, instruction)
    nextIp
}
