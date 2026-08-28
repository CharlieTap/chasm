package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32GtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32GtDispatcher(
    instruction: NumericInstruction.F32Gt,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32GtExecutor(vstack, context, instruction)
    nextIp
}
