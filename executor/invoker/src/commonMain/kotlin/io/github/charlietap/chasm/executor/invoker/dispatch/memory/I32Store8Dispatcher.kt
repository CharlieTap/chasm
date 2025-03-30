package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32Store8Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Store8Dispatcher(
    instruction: MemoryInstruction.I32Store8,
) = I32Store8Dispatcher(
    instruction = instruction,
    executor = ::I32Store8Executor,
)

internal inline fun I32Store8Dispatcher(
    instruction: MemoryInstruction.I32Store8,
    crossinline executor: Executor<MemoryInstruction.I32Store8>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
