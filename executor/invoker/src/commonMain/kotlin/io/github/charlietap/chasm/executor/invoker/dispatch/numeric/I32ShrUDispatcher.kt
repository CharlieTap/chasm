package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShrUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ShrUDispatcher(
    instruction: NumericInstruction.I32ShrU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32ShrUExecutor(vstack, context, instruction)
    nextIp
}
