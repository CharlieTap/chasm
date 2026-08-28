package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64Add128Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Add128Dispatcher(
    instruction: NumericInstruction.I64Add128,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Add128Executor(vstack, context, instruction)
    nextIp
}
