@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun TableGetExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableGet,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrameOrError().bind()
    val tableAddress = frame.state.module.tableAddress(instruction.tableIdx.index()).bind()
    val tableInstance = store.table(tableAddress).bind()

    val elementIndex = stack.popI32().bind()
    if (elementIndex >= tableInstance.elements.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    val referenceValue = tableInstance.elements.getOrNull(elementIndex).toResultOr {
        InvocationError.TableElementLookupFailed(elementIndex)
    }.bind()

    stack.push(Stack.Entry.Value(referenceValue))
}
