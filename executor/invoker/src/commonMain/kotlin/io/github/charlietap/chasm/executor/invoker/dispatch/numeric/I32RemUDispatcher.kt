package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RemUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32RemUDispatcher(
    instruction: NumericInstruction.I32RemU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32RemUExecutor(vstack, context, instruction)
    nextIp
}
