package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal fun TableInitExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableInit,
) {
    val stack = context.vstack
    val tableInstance = instruction.table
    val elementInstance = instruction.element

    val elementsToInitialise = stack.popI32()
    val segmentOffset = stack.popI32()
    val tableOffset = stack.popI32()

    val srcRange = segmentOffset..<(segmentOffset + elementsToInitialise)
    val dstRange = tableOffset..<(tableOffset + elementsToInitialise)

    if (
        elementsToInitialise < 0 ||
        segmentOffset < 0 ||
        tableOffset < 0 ||
        !elementInstance.elements.indices.contains(srcRange) ||
        !tableInstance.elements.indices.contains(dstRange)
    ) {
        throw InvocationException(InvocationError.Trap.TrapEncountered)
    }

    if (elementsToInitialise == 0) return

    elementInstance.elements.copyInto(tableInstance.elements, dstRange.first, srcRange.first, srcRange.last + 1)
}
