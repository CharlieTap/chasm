package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32LeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32LeDispatcher(
    instruction: NumericInstruction.F32Le,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32LeExecutor(vstack, context, instruction)
    nextIp
}
