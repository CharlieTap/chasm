package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64ClzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ClzDispatcher(
    instruction: NumericInstruction.I64Clz,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64ClzExecutor(vstack, context, instruction)
    nextIp
}
