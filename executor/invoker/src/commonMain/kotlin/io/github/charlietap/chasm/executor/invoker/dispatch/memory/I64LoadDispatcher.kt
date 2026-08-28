package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64LoadDispatcher(
    instruction: MemoryInstruction.I64Load,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64LoadExecutor(vstack, context, instruction)
    nextIp
}
