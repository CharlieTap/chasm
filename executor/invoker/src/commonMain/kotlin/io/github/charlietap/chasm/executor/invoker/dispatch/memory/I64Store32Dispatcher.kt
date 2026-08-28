package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Store32Dispatcher(
    instruction: MemoryInstruction.I64Store32,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Store32Executor(vstack, context, instruction)
    nextIp
}
