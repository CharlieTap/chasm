package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RotrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64RotrDispatcher(
    instruction: NumericInstruction.I64Rotr,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64RotrExecutor(vstack, context, instruction)
    nextIp
}
