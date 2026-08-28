package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32Store8Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Store8Dispatcher(
    instruction: MemoryInstruction.I32Store8,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32Store8Executor(vstack, context, instruction)
    nextIp
}
