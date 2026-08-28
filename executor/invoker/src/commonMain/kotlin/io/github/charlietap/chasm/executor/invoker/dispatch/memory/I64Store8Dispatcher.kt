package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store8Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Store8Dispatcher(
    instruction: MemoryInstruction.I64Store8,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Store8Executor(vstack, context, instruction)
    nextIp
}
