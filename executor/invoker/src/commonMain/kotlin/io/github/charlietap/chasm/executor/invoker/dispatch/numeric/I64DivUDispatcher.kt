package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64DivUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64DivUDispatcher(
    instruction: NumericInstruction.I64DivU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64DivUExecutor(vstack, context, instruction)
    nextIp
}
