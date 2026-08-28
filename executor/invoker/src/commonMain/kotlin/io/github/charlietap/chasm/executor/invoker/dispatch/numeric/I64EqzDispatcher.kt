package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop.I64EqzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64EqzDispatcher(
    instruction: NumericInstruction.I64Eqz,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64EqzExecutor(vstack, context, instruction)
    nextIp
}
