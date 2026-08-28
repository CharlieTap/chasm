package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64DivSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64DivSDispatcher(
    instruction: NumericInstruction.I64DivS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64DivSExecutor(vstack, context, instruction)
    nextIp
}
