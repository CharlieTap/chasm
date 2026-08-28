package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64CtzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64CtzDispatcher(
    instruction: NumericInstruction.I64Ctz,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64CtzExecutor(vstack, context, instruction)
    nextIp
}
