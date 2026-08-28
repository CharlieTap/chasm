package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncF32SDispatcher(
    instruction: NumericInstruction.I64TruncF32S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64TruncF32SExecutor(vstack, context, instruction)
    nextIp
}
