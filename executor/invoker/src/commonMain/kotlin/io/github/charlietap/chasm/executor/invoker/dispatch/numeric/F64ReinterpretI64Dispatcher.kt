package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ReinterpretI64Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ReinterpretI64Dispatcher(
    instruction: NumericInstruction.F64ReinterpretI64,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64ReinterpretI64Executor(vstack, context, instruction)
    nextIp
}
