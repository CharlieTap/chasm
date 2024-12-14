package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store16Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I64Store16Dispatcher(
    instruction: MemoryInstruction.I64Store16,
) = I64Store16Dispatcher(
    instruction = instruction,
    executor = ::I64Store16Executor,
)

internal inline fun I64Store16Dispatcher(
    instruction: MemoryInstruction.I64Store16,
    crossinline executor: Executor<MemoryInstruction.I64Store16>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
