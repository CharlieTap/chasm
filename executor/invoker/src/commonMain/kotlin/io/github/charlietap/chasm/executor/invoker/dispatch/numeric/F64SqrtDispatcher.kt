package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64SqrtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64SqrtDispatcher(
    instruction: NumericInstruction.F64Sqrt,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64SqrtExecutor(vstack, context, instruction)
    nextIp
}
