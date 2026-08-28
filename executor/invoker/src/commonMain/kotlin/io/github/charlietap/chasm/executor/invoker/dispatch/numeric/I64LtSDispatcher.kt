package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64LtSDispatcher(
    instruction: NumericInstruction.I64LtS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64LtSExecutor(vstack, context, instruction)
    nextIp
}
