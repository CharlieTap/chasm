@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal fun TableFillExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableFill,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context

    val frame = stack.peekFrame().bind()
    val tableAddress = frame.state.module.tableAddress(instruction.tableIdx).bind()
    val tableInstance = store.table(tableAddress).bind()

    val elementsToFill = stack.popI32().bind()
    val fillValue = stack.popReference().bind()
    val tableOffset = stack.popI32().bind()

    val fillRange = tableOffset..<(tableOffset + elementsToFill)

    if (!tableInstance.elements.indices.contains(fillRange)) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToFill == 0) return@binding

    tableInstance.elements.fill(fillValue, fillRange.first, fillRange.last + 1)
}
