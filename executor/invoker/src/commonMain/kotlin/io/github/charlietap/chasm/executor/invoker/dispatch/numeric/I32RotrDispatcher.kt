package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32RotrDispatcher(
    instruction: NumericInstruction.I32Rotr,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32RotrExecutor(vstack, context, instruction)
    nextIp
}
