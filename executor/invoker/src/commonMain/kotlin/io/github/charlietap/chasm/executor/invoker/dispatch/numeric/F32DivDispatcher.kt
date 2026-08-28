package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32DivExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32DivDispatcher(
    instruction: NumericInstruction.F32Div,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32DivExecutor(vstack, context, instruction)
    nextIp
}
