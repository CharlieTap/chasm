package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConvertI64UDispatcher(
    instruction: NumericInstruction.F32ConvertI64U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32ConvertI64UExecutor(vstack, context, instruction)
    nextIp
}
