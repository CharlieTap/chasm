package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncF64UDispatcher(
    instruction: NumericInstruction.I64TruncF64U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64TruncF64UExecutor(vstack, context, instruction)
    nextIp
}
