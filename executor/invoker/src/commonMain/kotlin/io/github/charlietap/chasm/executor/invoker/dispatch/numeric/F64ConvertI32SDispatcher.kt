package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ConvertI32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConvertI32SDispatcher(
    instruction: NumericInstruction.F64ConvertI32S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64ConvertI32SExecutor(vstack, context, instruction)
    nextIp
}
