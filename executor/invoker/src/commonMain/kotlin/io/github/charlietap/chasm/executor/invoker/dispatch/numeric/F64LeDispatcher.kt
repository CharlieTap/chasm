package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64LeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64LeDispatcher(
    instruction: NumericInstruction.F64Le,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64LeExecutor(vstack, context, instruction)
    nextIp
}
