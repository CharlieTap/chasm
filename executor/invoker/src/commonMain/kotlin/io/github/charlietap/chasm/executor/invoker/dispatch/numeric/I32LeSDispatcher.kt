package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32LeSDispatcher(
    instruction: NumericInstruction.I32LeS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32LeSExecutor(vstack, context, instruction)
    nextIp
}
