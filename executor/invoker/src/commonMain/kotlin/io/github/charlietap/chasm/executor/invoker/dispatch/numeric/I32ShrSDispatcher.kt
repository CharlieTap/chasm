package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShrSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ShrSDispatcher(
    instruction: NumericInstruction.I32ShrS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32ShrSExecutor(vstack, context, instruction)
    nextIp
}
