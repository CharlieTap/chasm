package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncSatF64UDispatcher(
    instruction: NumericInstruction.I64TruncSatF64U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64TruncSatF64UExecutor(vstack, context, instruction)
    nextIp
}
