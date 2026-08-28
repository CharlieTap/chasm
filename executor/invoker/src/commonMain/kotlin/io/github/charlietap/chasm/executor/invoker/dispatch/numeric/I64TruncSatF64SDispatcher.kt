package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncSatF64SDispatcher(
    instruction: NumericInstruction.I64TruncSatF64S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64TruncSatF64SExecutor(vstack, context, instruction)
    nextIp
}
