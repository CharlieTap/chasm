package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NegExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32NegDispatcher(
    instruction: NumericInstruction.F32Neg,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32NegExecutor(vstack, context, instruction)
    nextIp
}
