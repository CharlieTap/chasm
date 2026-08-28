package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32Extend8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32Extend8SDispatcher(
    instruction: NumericInstruction.I32Extend8S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32Extend8SExecutor(vstack, context, instruction)
    nextIp
}
