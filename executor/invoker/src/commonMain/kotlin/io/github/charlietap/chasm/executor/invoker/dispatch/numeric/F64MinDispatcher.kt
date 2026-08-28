package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MinExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64MinDispatcher(
    instruction: NumericInstruction.F64Min,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64MinExecutor(vstack, context, instruction)
    nextIp
}
