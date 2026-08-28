package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64FloorExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64FloorDispatcher(
    instruction: NumericInstruction.F64Floor,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64FloorExecutor(vstack, context, instruction)
    nextIp
}
