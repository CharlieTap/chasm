package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F64LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun F64LoadDispatcher(
    instruction: MemoryInstruction.F64Load,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F64LoadExecutor(vstack, context, instruction)
    nextIp
}
