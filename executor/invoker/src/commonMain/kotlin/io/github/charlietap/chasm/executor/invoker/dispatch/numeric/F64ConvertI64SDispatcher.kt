package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConvertI64SDispatcher(
    instruction: NumericInstruction.F64ConvertI64S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64ConvertI64SExecutor(vstack, context, instruction)
    nextIp
}
