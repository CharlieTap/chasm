package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.instruction.parametric.DropExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun DropDispatcher(
    instruction: ParametricInstruction.Drop,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    DropExecutor(vstack, context, instruction)
    nextIp
}
