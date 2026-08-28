package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32WrapI64Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32WrapI64Dispatcher(
    instruction: NumericInstruction.I32WrapI64,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32WrapI64Executor(vstack, context, instruction)
    nextIp
}
