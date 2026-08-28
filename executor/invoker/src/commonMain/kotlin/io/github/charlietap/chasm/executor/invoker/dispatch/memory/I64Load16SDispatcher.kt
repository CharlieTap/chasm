package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load16SDispatcher(
    instruction: MemoryInstruction.I64Load16S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Load16SExecutor(vstack, context, instruction)
    nextIp
}
