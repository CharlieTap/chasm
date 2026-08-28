package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Load16SDispatcher(
    instruction: MemoryInstruction.I32Load16S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32Load16SExecutor(vstack, context, instruction)
    nextIp
}
