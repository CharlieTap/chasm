package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RemUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64RemUDispatcher(
    instruction: NumericInstruction.I64RemU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64RemUExecutor(vstack, context, instruction)
    nextIp
}
