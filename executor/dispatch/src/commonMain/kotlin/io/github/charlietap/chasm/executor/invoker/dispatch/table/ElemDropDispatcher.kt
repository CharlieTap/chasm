package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.instruction.table.ElementDropExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun ElemDropDispatcher(
    instruction: TableInstruction.ElemDrop,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ElementDropExecutor(vstack, context, instruction)
    nextIp
}
