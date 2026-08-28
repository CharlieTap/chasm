package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32LtSDispatcher(
    instruction: NumericInstruction.I32LtS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32LtSExecutor(vstack, context, instruction)
    nextIp
}
