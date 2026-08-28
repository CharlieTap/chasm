package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Extend32SDispatcher(
    instruction: NumericInstruction.I64Extend32S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Extend32SExecutor(vstack, context, instruction)
    nextIp
}
