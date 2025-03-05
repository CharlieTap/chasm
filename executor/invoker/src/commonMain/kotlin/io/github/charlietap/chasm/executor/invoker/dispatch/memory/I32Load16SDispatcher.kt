package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Load16SDispatcher(
    instruction: MemoryInstruction.I32Load16S,
) = I32Load16SDispatcher(
    instruction = instruction,
    executor = ::I32Load16SExecutor,
)

internal inline fun I32Load16SDispatcher(
    instruction: MemoryInstruction.I32Load16S,
    crossinline executor: Executor<MemoryInstruction.I32Load16S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
