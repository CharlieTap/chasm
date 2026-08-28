package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulWideSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64MulWideSDispatcher(
    instruction: NumericInstruction.I64MulWideS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64MulWideSExecutor(vstack, context, instruction)
    nextIp
}
