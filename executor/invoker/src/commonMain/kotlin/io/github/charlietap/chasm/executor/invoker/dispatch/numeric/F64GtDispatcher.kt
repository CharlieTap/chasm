package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64GtDispatcher(
    instruction: NumericInstruction.F64Gt,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64GtExecutor(vstack, context, instruction)
    nextIp
}
