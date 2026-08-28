package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store16Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Store16Dispatcher(
    instruction: MemoryInstruction.I64Store16,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Store16Executor(vstack, context, instruction)
    nextIp
}
