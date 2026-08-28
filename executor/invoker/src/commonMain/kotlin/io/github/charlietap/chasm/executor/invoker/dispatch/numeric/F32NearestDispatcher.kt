package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NearestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32NearestDispatcher(
    instruction: NumericInstruction.F32Nearest,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32NearestExecutor(vstack, context, instruction)
    nextIp
}
