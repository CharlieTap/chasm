package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64LtUDispatcher(
    instruction: NumericInstruction.I64LtU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64LtUExecutor(vstack, context, instruction)
    nextIp
}
