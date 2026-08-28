package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64CopysignExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64CopysignDispatcher(
    instruction: NumericInstruction.F64Copysign,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64CopysignExecutor(vstack, context, instruction)
    nextIp
}
