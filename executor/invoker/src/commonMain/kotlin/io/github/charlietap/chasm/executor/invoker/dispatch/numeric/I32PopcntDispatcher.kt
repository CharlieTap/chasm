package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32PopcntExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32PopcntDispatcher(
    instruction: NumericInstruction.I32Popcnt,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32PopcntExecutor(vstack, context, instruction)
    nextIp
}
