package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32Store16Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I32Store16Dispatcher(
    instruction: MemoryInstruction.I32Store16,
) = I32Store16Dispatcher(
    instruction = instruction,
    executor = ::I32Store16Executor,
)

internal inline fun I32Store16Dispatcher(
    instruction: MemoryInstruction.I32Store16,
    crossinline executor: Executor<MemoryInstruction.I32Store16>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
