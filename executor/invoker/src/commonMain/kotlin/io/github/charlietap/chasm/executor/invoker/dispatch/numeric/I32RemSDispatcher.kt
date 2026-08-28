package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RemSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32RemSDispatcher(
    instruction: NumericInstruction.I32RemS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32RemSExecutor(vstack, context, instruction)
    nextIp
}
