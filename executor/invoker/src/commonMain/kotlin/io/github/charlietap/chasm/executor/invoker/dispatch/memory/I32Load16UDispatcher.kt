package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load16UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Load16UDispatcher(
    instruction: MemoryInstruction.I32Load16U,
) = I32Load16UDispatcher(
    instruction = instruction,
    executor = ::I32Load16UExecutor,
)

internal inline fun I32Load16UDispatcher(
    instruction: MemoryInstruction.I32Load16U,
    crossinline executor: Executor<MemoryInstruction.I32Load16U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
