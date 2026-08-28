package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ReinterpretF64Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ReinterpretF64Dispatcher(
    instruction: NumericInstruction.I64ReinterpretF64,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64ReinterpretF64Executor(vstack, context, instruction)
    nextIp
}
