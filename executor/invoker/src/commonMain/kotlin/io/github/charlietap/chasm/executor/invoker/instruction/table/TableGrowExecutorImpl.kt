@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.grow
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.invoker.ext.popReference
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun TableGrowExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableGrow,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrameOrError().bind()
    val tableAddress = frame.state.module.tableAddress(instruction.tableIdx.index()).bind()
    val tableInstance = store.table(tableAddress).bind()

    val tableSize = tableInstance.elements.size
    val elementsToAdd = stack.popI32().bind()
    val referenceValue = stack.popReference().bind()

    val result = tableInstance.grow(elementsToAdd, referenceValue)

    result.fold({ newInstance ->
        store.tables[tableAddress.address] = newInstance
        stack.push(Stack.Entry.Value(NumberValue.I32(tableSize)))
    }, {
        stack.push(Stack.Entry.Value(NumberValue.I32(-1)))
    })
}
