package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64CeilExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64CeilDispatcher(
    instruction: NumericInstruction.F64Ceil,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64CeilExecutor(vstack, context, instruction)
    nextIp
}
