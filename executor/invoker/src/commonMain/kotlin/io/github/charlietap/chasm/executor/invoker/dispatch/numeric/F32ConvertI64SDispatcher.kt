package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConvertI64SDispatcher(
    instruction: NumericInstruction.F32ConvertI64S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32ConvertI64SExecutor(vstack, context, instruction)
    nextIp
}
