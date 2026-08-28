package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Extend16SDispatcher(
    instruction: NumericInstruction.I64Extend16S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Extend16SExecutor(vstack, context, instruction)
    nextIp
}
