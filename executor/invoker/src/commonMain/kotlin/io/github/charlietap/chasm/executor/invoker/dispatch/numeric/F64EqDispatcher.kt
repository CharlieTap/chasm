package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64EqDispatcher(
    instruction: NumericInstruction.F64Eq,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64EqExecutor(vstack, context, instruction)
    nextIp
}
