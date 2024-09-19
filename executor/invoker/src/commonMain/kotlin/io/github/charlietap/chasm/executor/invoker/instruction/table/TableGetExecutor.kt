@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias TableGetExecutor = (Store, Stack, TableInstruction.TableGet) -> Result<Unit, InvocationError>

internal inline fun TableGetExecutor(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableGet,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val tableAddress = frame.state.module.tableAddress(instruction.tableIdx).bind()
    val tableInstance = store.table(tableAddress).bind()

    val elementIndex = stack.popI32().bind()
    if (elementIndex >= tableInstance.elements.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    val referenceValue = tableInstance.element(elementIndex).bind()

    stack.push(Stack.Entry.Value(referenceValue))
}
