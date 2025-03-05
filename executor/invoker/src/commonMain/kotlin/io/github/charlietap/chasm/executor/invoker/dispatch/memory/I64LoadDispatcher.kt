package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64LoadDispatcher(
    instruction: MemoryInstruction.I64Load,
) = I64LoadDispatcher(
    instruction = instruction,
    executor = ::I64LoadExecutor,
)

internal inline fun I64LoadDispatcher(
    instruction: MemoryInstruction.I64Load,
    crossinline executor: Executor<MemoryInstruction.I64Load>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
