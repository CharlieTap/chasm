package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64GeSDispatcher(
    instruction: NumericInstruction.I64GeS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64GeSExecutor(vstack, context, instruction)
    nextIp
}
