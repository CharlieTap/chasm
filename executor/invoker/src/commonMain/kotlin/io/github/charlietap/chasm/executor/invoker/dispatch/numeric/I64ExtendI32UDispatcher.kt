package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ExtendI32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ExtendI32UDispatcher(
    instruction: NumericInstruction.I64ExtendI32U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64ExtendI32UExecutor(vstack, context, instruction)
    nextIp
}
