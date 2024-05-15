@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun TableCopyExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableCopy,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val srcTableAddress = frame.state.module.tableAddress(instruction.srcTableIdx.index()).bind()
    val srcTableInstance = store.table(srcTableAddress).bind() // taby

    val dstTableAddress = frame.state.module.tableAddress(instruction.destTableIdx.index()).bind()
    val dstTableInstance = store.table(dstTableAddress).bind() // tabx

    val elementsToCopy = stack.popI32().bind()
    val srcOffset = stack.popI32().bind()
    val dstOffset = stack.popI32().bind()

    if (srcOffset + elementsToCopy > srcTableInstance.elements.size || dstOffset + elementsToCopy > dstTableInstance.elements.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToCopy == 0) return@binding

    val step = if (dstOffset <= srcOffset) 1 else -1
    val start = if (dstOffset <= srcOffset) 0 else elementsToCopy - 1

    repeat(elementsToCopy) { i ->
        val index = start + i * step
        dstTableInstance.elements[dstOffset + index] = srcTableInstance.elements[srcOffset + index]
    }
}
