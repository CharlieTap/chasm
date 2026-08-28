package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32CeilExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32CeilDispatcher(
    instruction: NumericInstruction.F32Ceil,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32CeilExecutor(vstack, context, instruction)
    nextIp
}
