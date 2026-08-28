package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32GeSDispatcher(
    instruction: NumericInstruction.I32GeS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32GeSExecutor(vstack, context, instruction)
    nextIp
}
