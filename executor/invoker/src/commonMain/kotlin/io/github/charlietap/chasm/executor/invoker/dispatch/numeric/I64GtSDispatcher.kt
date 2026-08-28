package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64GtSDispatcher(
    instruction: NumericInstruction.I64GtS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64GtSExecutor(vstack, context, instruction)
    nextIp
}
