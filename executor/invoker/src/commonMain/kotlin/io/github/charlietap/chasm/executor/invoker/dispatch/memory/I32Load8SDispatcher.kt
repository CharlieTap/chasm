package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Load8SDispatcher(
    instruction: MemoryInstruction.I32Load8S,
) = I32Load8SDispatcher(
    instruction = instruction,
    executor = ::I32Load8SExecutor,
)

internal inline fun I32Load8SDispatcher(
    instruction: MemoryInstruction.I32Load8S,
    crossinline executor: Executor<MemoryInstruction.I32Load8S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
