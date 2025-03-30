package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load8SDispatcher(
    instruction: MemoryInstruction.I64Load8S,
) = I64Load8SDispatcher(
    instruction = instruction,
    executor = ::I64Load8SExecutor,
)

internal inline fun I64Load8SDispatcher(
    instruction: MemoryInstruction.I64Load8S,
    crossinline executor: Executor<MemoryInstruction.I64Load8S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
