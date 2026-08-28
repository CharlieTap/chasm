package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64NeDispatcher(
    instruction: NumericInstruction.I64Ne,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64NeExecutor(vstack, context, instruction)
    nextIp
}
