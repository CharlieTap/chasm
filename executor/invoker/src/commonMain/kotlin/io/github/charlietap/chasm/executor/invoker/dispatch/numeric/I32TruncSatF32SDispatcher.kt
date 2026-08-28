package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncSatF32SDispatcher(
    instruction: NumericInstruction.I32TruncSatF32S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32TruncSatF32SExecutor(vstack, context, instruction)
    nextIp
}
