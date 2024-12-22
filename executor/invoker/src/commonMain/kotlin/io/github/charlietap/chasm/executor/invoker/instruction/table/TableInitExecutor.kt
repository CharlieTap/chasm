package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal fun TableInitExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableInit,
): Result<Unit, InvocationError> = binding {

    val (stack) = context

    val tableInstance = instruction.table
    val elementInstance = instruction.element

    val elementsToInitialise = stack.popI32().bind()
    val segmentOffset = stack.popI32().bind()
    val tableOffset = stack.popI32().bind()

    val srcRange = segmentOffset..<(segmentOffset + elementsToInitialise)
    val dstRange = tableOffset..<(tableOffset + elementsToInitialise)

    if (
        elementsToInitialise < 0 ||
        segmentOffset < 0 ||
        tableOffset < 0 ||
        !elementInstance.elements.indices.contains(srcRange) ||
        !tableInstance.elements.indices.contains(dstRange)
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToInitialise == 0) return@binding

    elementInstance.elements.copyInto(tableInstance.elements, dstRange.first, srcRange.first, srcRange.last + 1)
}
