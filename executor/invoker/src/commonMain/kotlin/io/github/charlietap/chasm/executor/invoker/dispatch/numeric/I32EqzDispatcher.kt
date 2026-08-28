package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop.I32EqzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32EqzDispatcher(
    instruction: NumericInstruction.I32Eqz,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32EqzExecutor(vstack, context, instruction)
    nextIp
}
