package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32LtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32LtDispatcher(
    instruction: NumericInstruction.F32Lt,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32LtExecutor(vstack, context, instruction)
    nextIp
}
