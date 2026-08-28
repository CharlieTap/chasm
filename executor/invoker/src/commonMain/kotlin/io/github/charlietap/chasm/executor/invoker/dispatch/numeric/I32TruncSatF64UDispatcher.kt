package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncSatF64UDispatcher(
    instruction: NumericInstruction.I32TruncSatF64U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32TruncSatF64UExecutor(vstack, context, instruction)
    nextIp
}
