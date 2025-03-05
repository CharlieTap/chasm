package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

internal inline fun TableGetExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableGet,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    val elementIndex = stack.popI32()

    stack.push(tableInstance.element(elementIndex))
}
