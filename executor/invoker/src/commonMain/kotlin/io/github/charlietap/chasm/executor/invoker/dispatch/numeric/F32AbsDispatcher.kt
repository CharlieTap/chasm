package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32AbsExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32AbsDispatcher(
    instruction: NumericInstruction.F32Abs,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32AbsExecutor(vstack, context, instruction)
    nextIp
}
