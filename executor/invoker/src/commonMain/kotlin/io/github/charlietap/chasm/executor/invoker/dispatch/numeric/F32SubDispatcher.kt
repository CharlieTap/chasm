package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32SubExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32SubDispatcher(
    instruction: NumericInstruction.F32Sub,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32SubExecutor(vstack, context, instruction)
    nextIp
}
