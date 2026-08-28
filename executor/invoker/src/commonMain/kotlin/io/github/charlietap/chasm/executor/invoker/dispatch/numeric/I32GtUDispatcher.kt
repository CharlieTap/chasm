package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32GtUDispatcher(
    instruction: NumericInstruction.I32GtU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32GtUExecutor(vstack, context, instruction)
    nextIp
}
