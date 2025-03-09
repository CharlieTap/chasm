package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32NeDispatcher(
    instruction: FusedNumericInstruction.F32Ne,
) = F32NeDispatcher(
    instruction = instruction,
    executor = ::F32NeExecutor,
)

internal inline fun F32NeDispatcher(
    instruction: FusedNumericInstruction.F32Ne,
    crossinline executor: Executor<FusedNumericInstruction.F32Ne>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
