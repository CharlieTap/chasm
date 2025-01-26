package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal inline fun ElementDropExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.ElemDrop,
) {
    instruction.element.elements = longArrayOf()
}
