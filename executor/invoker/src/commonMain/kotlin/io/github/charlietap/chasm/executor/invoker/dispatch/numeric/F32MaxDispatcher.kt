package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MaxExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32MaxDispatcher(
    instruction: NumericInstruction.F32Max,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32MaxExecutor(vstack, context, instruction)
    nextIp
}
