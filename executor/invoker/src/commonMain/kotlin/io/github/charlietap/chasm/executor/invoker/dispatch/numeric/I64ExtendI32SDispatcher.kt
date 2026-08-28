package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ExtendI32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ExtendI32SDispatcher(
    instruction: NumericInstruction.I64ExtendI32S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64ExtendI32SExecutor(vstack, context, instruction)
    nextIp
}
