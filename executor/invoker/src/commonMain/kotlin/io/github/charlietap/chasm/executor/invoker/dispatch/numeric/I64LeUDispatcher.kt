package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64LeUDispatcher(
    instruction: NumericInstruction.I64LeU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64LeUExecutor(vstack, context, instruction)
    nextIp
}
