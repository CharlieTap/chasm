package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32AndExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32AndDispatcher(
    instruction: NumericInstruction.I32And,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32AndExecutor(vstack, context, instruction)
    nextIp
}
