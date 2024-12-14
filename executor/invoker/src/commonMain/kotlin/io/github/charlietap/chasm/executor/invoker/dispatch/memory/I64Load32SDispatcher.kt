package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load32SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I64Load32SDispatcher(
    instruction: MemoryInstruction.I64Load32S,
) = I64Load32SDispatcher(
    instruction = instruction,
    executor = ::I64Load32SExecutor,
)

internal inline fun I64Load32SDispatcher(
    instruction: MemoryInstruction.I64Load32S,
    crossinline executor: Executor<MemoryInstruction.I64Load32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
