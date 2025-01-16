package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal inline fun TableGetExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableGet,
) {

    val (stack) = context
    val tableInstance = instruction.table

    val elementIndex = stack.popI32()
    val referenceValue = tableInstance.element(elementIndex)

    stack.push(referenceValue)
}
