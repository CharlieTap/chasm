package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F32LoadExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun F32LoadDispatcher(
    instruction: MemoryInstruction.F32Load,
) = F32LoadDispatcher(
    instruction = instruction,
    executor = ::F32LoadExecutor,
)

internal inline fun F32LoadDispatcher(
    instruction: MemoryInstruction.F32Load,
    crossinline executor: Executor<MemoryInstruction.F32Load>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
