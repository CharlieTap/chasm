package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32AddDispatcher(
    instruction: NumericInstruction.I32Add,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32AddExecutor(vstack, context, instruction)
    nextIp
}
