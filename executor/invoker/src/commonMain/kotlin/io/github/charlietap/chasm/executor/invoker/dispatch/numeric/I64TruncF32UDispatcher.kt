package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncF32UDispatcher(
    instruction: NumericInstruction.I64TruncF32U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64TruncF32UExecutor(vstack, context, instruction)
    nextIp
}
