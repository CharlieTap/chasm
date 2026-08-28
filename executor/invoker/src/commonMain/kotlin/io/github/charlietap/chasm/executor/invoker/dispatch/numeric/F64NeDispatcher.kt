package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64NeDispatcher(
    instruction: NumericInstruction.F64Ne,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64NeExecutor(vstack, context, instruction)
    nextIp
}
