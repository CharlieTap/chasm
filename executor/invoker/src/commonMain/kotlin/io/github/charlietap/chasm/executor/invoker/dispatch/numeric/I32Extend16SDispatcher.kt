package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32Extend16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32Extend16SDispatcher(
    instruction: NumericInstruction.I32Extend16S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32Extend16SExecutor(vstack, context, instruction)
    nextIp
}
