package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun TableGetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableGet,
) {
    val tableInstance = instruction.table
    val elementIndex = vstack.popI32()

    vstack.push(tableInstance.element(elementIndex))
}
