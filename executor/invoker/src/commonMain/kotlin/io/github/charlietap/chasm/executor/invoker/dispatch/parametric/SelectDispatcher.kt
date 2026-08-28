package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.instruction.parametric.SelectExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun SelectDispatcher(
    instruction: ParametricInstruction.Select,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    SelectExecutor(vstack, context, instruction)
    nextIp
}
