package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64LeSDispatcher(
    instruction: NumericInstruction.I64LeS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64LeSExecutor(vstack, context, instruction)
    nextIp
}
