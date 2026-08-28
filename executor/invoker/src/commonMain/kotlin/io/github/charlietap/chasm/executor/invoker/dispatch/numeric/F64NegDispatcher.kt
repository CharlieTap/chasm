package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64NegExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64NegDispatcher(
    instruction: NumericInstruction.F64Neg,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64NegExecutor(vstack, context, instruction)
    nextIp
}
