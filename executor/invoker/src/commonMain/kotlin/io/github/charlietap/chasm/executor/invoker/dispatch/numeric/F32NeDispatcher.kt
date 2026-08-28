package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32NeDispatcher(
    instruction: NumericInstruction.F32Ne,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32NeExecutor(vstack, context, instruction)
    nextIp
}
