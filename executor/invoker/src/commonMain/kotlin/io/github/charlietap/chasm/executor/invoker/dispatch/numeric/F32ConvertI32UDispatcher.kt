package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConvertI32UDispatcher(
    instruction: NumericInstruction.F32ConvertI32U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32ConvertI32UExecutor(vstack, context, instruction)
    nextIp
}
