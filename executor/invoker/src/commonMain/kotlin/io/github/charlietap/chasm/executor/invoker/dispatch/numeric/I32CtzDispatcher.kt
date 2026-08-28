package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32CtzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32CtzDispatcher(
    instruction: NumericInstruction.I32Ctz,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32CtzExecutor(vstack, context, instruction)
    nextIp
}
