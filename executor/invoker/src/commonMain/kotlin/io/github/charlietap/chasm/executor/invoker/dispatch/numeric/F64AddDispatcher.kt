package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64AddDispatcher(
    instruction: NumericInstruction.F64Add,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64AddExecutor(vstack, context, instruction)
    nextIp
}
