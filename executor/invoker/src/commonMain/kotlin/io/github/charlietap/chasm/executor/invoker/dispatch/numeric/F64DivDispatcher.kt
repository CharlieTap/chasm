package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64DivExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64DivDispatcher(
    instruction: NumericInstruction.F64Div,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64DivExecutor(vstack, context, instruction)
    nextIp
}
