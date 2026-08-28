package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64GtUDispatcher(
    instruction: NumericInstruction.I64GtU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64GtUExecutor(vstack, context, instruction)
    nextIp
}
