package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32GeUDispatcher(
    instruction: NumericInstruction.I32GeU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32GeUExecutor(vstack, context, instruction)
    nextIp
}
