package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64NeDispatcher(
    instruction: NumericInstruction.F64Ne,
) = F64NeDispatcher(
    instruction = instruction,
    executor = ::F64NeExecutor,
)

internal inline fun F64NeDispatcher(
    instruction: NumericInstruction.F64Ne,
    crossinline executor: Executor<NumericInstruction.F64Ne>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
