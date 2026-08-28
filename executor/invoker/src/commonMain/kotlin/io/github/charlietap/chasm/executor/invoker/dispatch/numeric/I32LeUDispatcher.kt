package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32LeUDispatcher(
    instruction: NumericInstruction.I32LeU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32LeUExecutor(vstack, context, instruction)
    nextIp
}
