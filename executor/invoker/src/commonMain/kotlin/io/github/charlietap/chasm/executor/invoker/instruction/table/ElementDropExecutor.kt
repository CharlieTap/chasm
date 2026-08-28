package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun ElementDropExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.ElemDrop,
) {
    instruction.element.elements = longArrayOf()
}
