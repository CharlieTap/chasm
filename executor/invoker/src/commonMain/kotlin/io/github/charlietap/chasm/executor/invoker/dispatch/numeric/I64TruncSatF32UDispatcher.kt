package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncSatF32UDispatcher(
    instruction: NumericInstruction.I64TruncSatF32U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64TruncSatF32UExecutor(vstack, context, instruction)
    nextIp
}
