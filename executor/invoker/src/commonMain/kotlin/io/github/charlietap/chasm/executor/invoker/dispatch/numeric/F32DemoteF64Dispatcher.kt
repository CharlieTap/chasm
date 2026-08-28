package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32DemoteF64Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32DemoteF64Dispatcher(
    instruction: NumericInstruction.F32DemoteF64,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32DemoteF64Executor(vstack, context, instruction)
    nextIp
}
