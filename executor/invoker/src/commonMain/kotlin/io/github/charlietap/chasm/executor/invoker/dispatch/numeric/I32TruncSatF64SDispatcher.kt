package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncSatF64SDispatcher(
    instruction: NumericInstruction.I32TruncSatF64S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32TruncSatF64SExecutor(vstack, context, instruction)
    nextIp
}
