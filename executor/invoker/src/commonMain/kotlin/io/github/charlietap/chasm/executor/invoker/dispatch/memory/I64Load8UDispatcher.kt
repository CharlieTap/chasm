package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load8UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load8UDispatcher(
    instruction: MemoryInstruction.I64Load8U,
) = I64Load8UDispatcher(
    instruction = instruction,
    executor = ::I64Load8UExecutor,
)

internal inline fun I64Load8UDispatcher(
    instruction: MemoryInstruction.I64Load8U,
    crossinline executor: Executor<MemoryInstruction.I64Load8U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
