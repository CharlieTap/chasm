package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32NeDispatcher(
    instruction: NumericInstruction.I32Ne,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32NeExecutor(vstack, context, instruction)
    nextIp
}
