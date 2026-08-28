package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64PromoteF32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64PromoteF32Dispatcher(
    instruction: NumericInstruction.F64PromoteF32,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64PromoteF32Executor(vstack, context, instruction)
    nextIp
}
