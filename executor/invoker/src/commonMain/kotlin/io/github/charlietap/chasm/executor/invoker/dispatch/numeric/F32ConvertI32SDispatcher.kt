package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConvertI32SDispatcher(
    instruction: NumericInstruction.F32ConvertI32S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32ConvertI32SExecutor(vstack, context, instruction)
    nextIp
}
