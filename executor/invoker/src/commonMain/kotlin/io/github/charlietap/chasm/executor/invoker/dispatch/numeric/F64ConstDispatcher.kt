package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop.F64ConstExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64ConstDispatcher(
    instruction: NumericInstruction.F64Const,
) = F64ConstDispatcher(
    instruction = instruction,
    executor = ::F64ConstExecutor,
)

internal inline fun F64ConstDispatcher(
    instruction: NumericInstruction.F64Const,
    crossinline executor: Executor<NumericInstruction.F64Const>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
