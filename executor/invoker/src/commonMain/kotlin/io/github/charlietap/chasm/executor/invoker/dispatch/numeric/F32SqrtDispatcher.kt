package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32SqrtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32SqrtDispatcher(
    instruction: NumericInstruction.F32Sqrt,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32SqrtExecutor(vstack, context, instruction)
    nextIp
}
