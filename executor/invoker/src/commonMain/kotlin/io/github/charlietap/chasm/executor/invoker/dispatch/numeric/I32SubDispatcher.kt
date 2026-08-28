package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32SubExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32SubDispatcher(
    instruction: NumericInstruction.I32Sub,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32SubExecutor(vstack, context, instruction)
    nextIp
}
