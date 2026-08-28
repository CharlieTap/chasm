package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32CopysignExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32CopysignDispatcher(
    instruction: NumericInstruction.F32Copysign,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32CopysignExecutor(vstack, context, instruction)
    nextIp
}
