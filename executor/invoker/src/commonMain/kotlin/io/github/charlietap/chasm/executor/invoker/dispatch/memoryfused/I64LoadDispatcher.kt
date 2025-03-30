package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64LoadDispatcher(
    instruction: FusedMemoryInstruction.I64Load,
) = I64LoadDispatcher(
    instruction = instruction,
    executor = ::I64LoadExecutor,
)

internal inline fun I64LoadDispatcher(
    instruction: FusedMemoryInstruction.I64Load,
    crossinline executor: Executor<FusedMemoryInstruction.I64Load>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
