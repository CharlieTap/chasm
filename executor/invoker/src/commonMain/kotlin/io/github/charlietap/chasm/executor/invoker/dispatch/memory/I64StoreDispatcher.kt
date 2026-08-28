package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64StoreDispatcher(
    instruction: MemoryInstruction.I64Store,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64StoreExecutor(vstack, context, instruction)
    nextIp
}
