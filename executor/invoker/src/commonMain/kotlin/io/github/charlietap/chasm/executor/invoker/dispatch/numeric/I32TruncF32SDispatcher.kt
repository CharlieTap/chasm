package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncF32SDispatcher(
    instruction: NumericInstruction.I32TruncF32S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32TruncF32SExecutor(vstack, context, instruction)
    nextIp
}
