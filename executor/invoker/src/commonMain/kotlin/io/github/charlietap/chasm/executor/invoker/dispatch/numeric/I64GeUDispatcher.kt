package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64GeUDispatcher(
    instruction: NumericInstruction.I64GeU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64GeUExecutor(vstack, context, instruction)
    nextIp
}
