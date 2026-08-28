package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64AndExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64AndDispatcher(
    instruction: NumericInstruction.I64And,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64AndExecutor(vstack, context, instruction)
    nextIp
}
