package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32StoreDispatcher(
    instruction: MemoryInstruction.I32Store,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32StoreExecutor(vstack, context, instruction)
    nextIp
}
