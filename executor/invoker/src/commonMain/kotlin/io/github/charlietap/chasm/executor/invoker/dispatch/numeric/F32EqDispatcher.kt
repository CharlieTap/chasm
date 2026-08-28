package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32EqDispatcher(
    instruction: NumericInstruction.F32Eq,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32EqExecutor(vstack, context, instruction)
    nextIp
}
