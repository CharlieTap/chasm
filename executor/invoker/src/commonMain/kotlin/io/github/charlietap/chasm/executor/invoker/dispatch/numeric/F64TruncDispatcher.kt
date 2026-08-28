package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64TruncExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64TruncDispatcher(
    instruction: NumericInstruction.F64Trunc,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64TruncExecutor(vstack, context, instruction)
    nextIp
}
