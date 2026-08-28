package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConvertI32UDispatcher(
    instruction: NumericInstruction.F64ConvertI32U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64ConvertI32UExecutor(vstack, context, instruction)
    nextIp
}
