package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64SubExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64SubDispatcher(
    instruction: NumericInstruction.F64Sub,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64SubExecutor(vstack, context, instruction)
    nextIp
}
