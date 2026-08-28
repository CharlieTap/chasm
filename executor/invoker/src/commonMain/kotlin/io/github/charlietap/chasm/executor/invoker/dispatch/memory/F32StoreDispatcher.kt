package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F32StoreExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun F32StoreDispatcher(
    instruction: MemoryInstruction.F32Store,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32StoreExecutor(vstack, context, instruction)
    nextIp
}
