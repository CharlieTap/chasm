@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun TableSizeExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableSize,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val tableAddress = frame.state.module.tableAddress(instruction.tableIdx).bind()
    val tableInstance = store.table(tableAddress).bind()

    val tableSize = tableInstance.elements.size

    stack.push(Stack.Entry.Value(NumberValue.I32(tableSize)))
}
