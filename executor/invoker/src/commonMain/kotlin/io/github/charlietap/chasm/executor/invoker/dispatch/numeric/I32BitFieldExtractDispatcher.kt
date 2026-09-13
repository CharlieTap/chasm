package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32BitFieldExtractExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32BitFieldExtractDispatcher(
    instruction: NumericInstruction.I32BitFieldExtractS,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, context, nextIp ->
        I32BitFieldExtractExecutor(vstack, context, instruction)
        nextIp
    }
}
