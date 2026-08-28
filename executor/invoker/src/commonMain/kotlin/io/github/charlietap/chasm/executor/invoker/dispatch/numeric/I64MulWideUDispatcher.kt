package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulWideUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64MulWideUDispatcher(
    instruction: NumericInstruction.I64MulWideU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64MulWideUExecutor(vstack, context, instruction)
    nextIp
}
