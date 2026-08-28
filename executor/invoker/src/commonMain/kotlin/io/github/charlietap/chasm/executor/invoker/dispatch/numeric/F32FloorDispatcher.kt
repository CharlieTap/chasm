package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32FloorExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32FloorDispatcher(
    instruction: NumericInstruction.F32Floor,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32FloorExecutor(vstack, context, instruction)
    nextIp
}
