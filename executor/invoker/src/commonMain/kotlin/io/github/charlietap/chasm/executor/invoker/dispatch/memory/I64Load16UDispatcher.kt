package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load16UExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I64Load16UDispatcher(
    instruction: MemoryInstruction.I64Load16U,
) = I64Load16UDispatcher(
    instruction = instruction,
    executor = ::I64Load16UExecutor,
)

internal inline fun I64Load16UDispatcher(
    instruction: MemoryInstruction.I64Load16U,
    crossinline executor: Executor<MemoryInstruction.I64Load16U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
