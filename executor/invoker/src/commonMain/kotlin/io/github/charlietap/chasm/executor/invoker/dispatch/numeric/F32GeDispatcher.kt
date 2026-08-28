package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32GeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32GeDispatcher(
    instruction: NumericInstruction.F32Ge,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32GeExecutor(vstack, context, instruction)
    nextIp
}
