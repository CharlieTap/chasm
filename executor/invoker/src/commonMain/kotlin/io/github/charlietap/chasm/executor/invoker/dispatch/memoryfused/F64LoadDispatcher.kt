package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.F64LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun F64LoadDispatcher(
    instruction: FusedMemoryInstruction.F64Load,
) = F64LoadDispatcher(
    instruction = instruction,
    executor = ::F64LoadExecutor,
)

internal inline fun F64LoadDispatcher(
    instruction: FusedMemoryInstruction.F64Load,
    crossinline executor: Executor<FusedMemoryInstruction.F64Load>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
