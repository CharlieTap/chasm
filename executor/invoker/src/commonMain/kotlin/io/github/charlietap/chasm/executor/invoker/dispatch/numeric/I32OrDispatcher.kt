package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32OrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32OrDispatcher(
    instruction: NumericInstruction.I32Or,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32OrExecutor(vstack, context, instruction)
    nextIp
}
