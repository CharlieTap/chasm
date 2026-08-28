package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32GtSDispatcher(
    instruction: NumericInstruction.I32GtS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32GtSExecutor(vstack, context, instruction)
    nextIp
}
