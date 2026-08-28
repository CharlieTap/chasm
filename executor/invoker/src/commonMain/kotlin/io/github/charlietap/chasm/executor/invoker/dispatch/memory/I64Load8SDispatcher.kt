package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load8SDispatcher(
    instruction: MemoryInstruction.I64Load8S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Load8SExecutor(vstack, context, instruction)
    nextIp
}
