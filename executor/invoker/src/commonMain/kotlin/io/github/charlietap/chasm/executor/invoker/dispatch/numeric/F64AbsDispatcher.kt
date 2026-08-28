package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64AbsExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64AbsDispatcher(
    instruction: NumericInstruction.F64Abs,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64AbsExecutor(vstack, context, instruction)
    nextIp
}
