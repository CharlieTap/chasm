package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load32SDispatcher(
    instruction: MemoryInstruction.I64Load32S,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Load32SExecutor(vstack, context, instruction)
    nextIp
}
