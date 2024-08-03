@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun TableInitExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableInit,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val tableAddress = frame.state.module.tableAddress(instruction.tableIdx).bind()
    val tableInstance = store.table(tableAddress).bind()

    val elementAddress = frame.state.module.elementAddress(instruction.elemIdx).bind()
    val elementInstance = store.element(elementAddress).bind()

    val elementsToInitialise = stack.popI32().bind()
    val segmentOffset = stack.popI32().bind()
    val tableOffset = stack.popI32().bind()

    val srcRange = segmentOffset..<(segmentOffset + elementsToInitialise)
    val dstRange = tableOffset..<(tableOffset + elementsToInitialise)

    if (
        elementsToInitialise < 0 || segmentOffset < 0 || tableOffset < 0 ||
        !elementInstance.elements.indices.contains(srcRange) ||
        !tableInstance.elements.indices.contains(dstRange)
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToInitialise == 0) return@binding

    elementInstance.elements.copyInto(tableInstance.elements, dstRange.first, srcRange.first, srcRange.last + 1)
}
