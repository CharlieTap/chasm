package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Extend8SDispatcher(
    instruction: NumericInstruction.I64Extend8S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Extend8SExecutor(vstack, context, instruction)
    nextIp
}
