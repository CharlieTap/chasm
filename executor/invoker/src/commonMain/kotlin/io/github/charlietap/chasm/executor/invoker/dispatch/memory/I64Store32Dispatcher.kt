package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store32Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I64Store32Dispatcher(
    instruction: MemoryInstruction.I64Store32,
) = I64Store32Dispatcher(
    instruction = instruction,
    executor = ::I64Store32Executor,
)

internal inline fun I64Store32Dispatcher(
    instruction: MemoryInstruction.I64Store32,
    crossinline executor: Executor<MemoryInstruction.I64Store32>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
