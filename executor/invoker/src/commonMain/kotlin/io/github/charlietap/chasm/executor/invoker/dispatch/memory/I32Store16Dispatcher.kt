package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32Store16Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Store16Dispatcher(
    instruction: MemoryInstruction.I32Store16,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32Store16Executor(vstack, context, instruction)
    nextIp
}
