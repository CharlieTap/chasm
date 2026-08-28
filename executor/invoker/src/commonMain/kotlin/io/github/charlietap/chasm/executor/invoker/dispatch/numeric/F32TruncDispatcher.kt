package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32TruncExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32TruncDispatcher(
    instruction: NumericInstruction.F32Trunc,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32TruncExecutor(vstack, context, instruction)
    nextIp
}
