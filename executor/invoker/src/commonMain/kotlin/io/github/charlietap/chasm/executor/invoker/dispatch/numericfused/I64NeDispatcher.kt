package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64NeDispatcher(
    instruction: FusedNumericInstruction.I64Ne,
) = I64NeDispatcher(
    instruction = instruction,
    executor = ::I64NeExecutor,
)

internal inline fun I64NeDispatcher(
    instruction: FusedNumericInstruction.I64Ne,
    crossinline executor: Executor<FusedNumericInstruction.I64Ne>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
