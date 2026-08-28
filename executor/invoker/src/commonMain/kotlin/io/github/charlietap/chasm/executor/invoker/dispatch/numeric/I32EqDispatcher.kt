package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32EqDispatcher(
    instruction: NumericInstruction.I32Eq,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32EqExecutor(vstack, context, instruction)
    nextIp
}
