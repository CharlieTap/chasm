package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConvertI64UDispatcher(
    instruction: NumericInstruction.F64ConvertI64U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64ConvertI64UExecutor(vstack, context, instruction)
    nextIp
}
