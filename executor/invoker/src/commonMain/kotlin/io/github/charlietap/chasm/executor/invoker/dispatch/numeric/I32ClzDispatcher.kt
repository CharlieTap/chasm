package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32ClzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ClzDispatcher(
    instruction: NumericInstruction.I32Clz,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32ClzExecutor(vstack, context, instruction)
    nextIp
}
