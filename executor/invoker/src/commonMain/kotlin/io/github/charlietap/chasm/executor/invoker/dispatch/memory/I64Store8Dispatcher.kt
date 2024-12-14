package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store8Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I64Store8Dispatcher(
    instruction: MemoryInstruction.I64Store8,
) = I64Store8Dispatcher(
    instruction = instruction,
    executor = ::I64Store8Executor,
)

internal inline fun I64Store8Dispatcher(
    instruction: MemoryInstruction.I64Store8,
    crossinline executor: Executor<MemoryInstruction.I64Store8>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
