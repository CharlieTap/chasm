package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load32UDispatcher(
    instruction: MemoryInstruction.I64Load32U,
) = I64Load32UDispatcher(
    instruction = instruction,
    executor = ::I64Load32UExecutor,
)

internal inline fun I64Load32UDispatcher(
    instruction: MemoryInstruction.I64Load32U,
    crossinline executor: Executor<MemoryInstruction.I64Load32U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
