package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64LtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64LtDispatcher(
    instruction: NumericInstruction.F64Lt,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64LtExecutor(vstack, context, instruction)
    nextIp
}
