package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncSatF32UDispatcher(
    instruction: NumericInstruction.I32TruncSatF32U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32TruncSatF32UExecutor(vstack, context, instruction)
    nextIp
}
