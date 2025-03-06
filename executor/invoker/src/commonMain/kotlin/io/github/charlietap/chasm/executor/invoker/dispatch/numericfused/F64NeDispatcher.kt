package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64NeDispatcher(
    instruction: FusedNumericInstruction.F64Ne,
) = F64NeDispatcher(
    instruction = instruction,
    executor = ::F64NeExecutor,
)

internal inline fun F64NeDispatcher(
    instruction: FusedNumericInstruction.F64Ne,
    crossinline executor: Executor<FusedNumericInstruction.F64Ne>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
