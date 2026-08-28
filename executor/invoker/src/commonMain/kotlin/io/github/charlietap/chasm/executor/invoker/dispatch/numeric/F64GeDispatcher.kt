package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64GeDispatcher(
    instruction: NumericInstruction.F64Ge,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64GeExecutor(vstack, context, instruction)
    nextIp
}
