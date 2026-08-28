package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RemSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64RemSDispatcher(
    instruction: NumericInstruction.I64RemS,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64RemSExecutor(vstack, context, instruction)
    nextIp
}
