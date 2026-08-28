package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncF32UDispatcher(
    instruction: NumericInstruction.I32TruncF32U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32TruncF32UExecutor(vstack, context, instruction)
    nextIp
}
