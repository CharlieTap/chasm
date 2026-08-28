package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64ShrUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ShrUDispatcher(
    instruction: NumericInstruction.I64ShrU,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64ShrUExecutor(vstack, context, instruction)
    nextIp
}
