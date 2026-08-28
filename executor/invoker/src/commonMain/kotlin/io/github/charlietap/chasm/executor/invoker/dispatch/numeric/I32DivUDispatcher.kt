package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32DivUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32DivUDispatcher(
    instruction: NumericInstruction.I32DivU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32DivUExecutor(vstack, context, instruction)
    nextIp
}
