package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MaxExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64MaxDispatcher(
    instruction: NumericInstruction.F64Max,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64MaxExecutor(vstack, context, instruction)
    nextIp
}
