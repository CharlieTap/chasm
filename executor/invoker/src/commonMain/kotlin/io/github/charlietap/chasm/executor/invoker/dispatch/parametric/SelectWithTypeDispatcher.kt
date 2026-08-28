package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.instruction.parametric.SelectWithTypeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun SelectWithTypeDispatcher(
    instruction: ParametricInstruction.SelectWithType,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    SelectWithTypeExecutor(vstack, context, instruction)
    nextIp
}
