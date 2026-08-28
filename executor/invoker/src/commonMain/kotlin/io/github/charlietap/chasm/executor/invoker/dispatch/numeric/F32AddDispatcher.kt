package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32AddDispatcher(
    instruction: NumericInstruction.F32Add,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32AddExecutor(vstack, context, instruction)
    nextIp
}
