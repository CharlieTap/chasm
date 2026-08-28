package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64OrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64OrDispatcher(
    instruction: NumericInstruction.I64Or,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64OrExecutor(vstack, context, instruction)
    nextIp
}
