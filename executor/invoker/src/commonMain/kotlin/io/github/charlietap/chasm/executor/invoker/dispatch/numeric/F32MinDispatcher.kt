package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MinExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32MinDispatcher(
    instruction: NumericInstruction.F32Min,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32MinExecutor(vstack, context, instruction)
    nextIp
}
