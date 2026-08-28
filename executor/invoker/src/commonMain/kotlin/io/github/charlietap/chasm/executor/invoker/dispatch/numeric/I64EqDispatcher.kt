package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64EqDispatcher(
    instruction: NumericInstruction.I64Eq,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64EqExecutor(vstack, context, instruction)
    nextIp
}
