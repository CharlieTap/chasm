package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncF64SDispatcher(
    instruction: NumericInstruction.I32TruncF64S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32TruncF64SExecutor(vstack, context, instruction)
    nextIp
}
