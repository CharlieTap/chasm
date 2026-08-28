package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64AddDispatcher(
    instruction: NumericInstruction.I64Add,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64AddExecutor(vstack, context, instruction)
    nextIp
}
