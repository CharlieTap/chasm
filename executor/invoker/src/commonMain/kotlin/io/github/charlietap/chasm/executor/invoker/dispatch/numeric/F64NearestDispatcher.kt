package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64NearestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64NearestDispatcher(
    instruction: NumericInstruction.F64Nearest,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64NearestExecutor(vstack, context, instruction)
    nextIp
}
