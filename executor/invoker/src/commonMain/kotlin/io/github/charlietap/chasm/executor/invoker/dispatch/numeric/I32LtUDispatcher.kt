package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32LtUDispatcher(
    instruction: NumericInstruction.I32LtU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32LtUExecutor(vstack, context, instruction)
    nextIp
}
