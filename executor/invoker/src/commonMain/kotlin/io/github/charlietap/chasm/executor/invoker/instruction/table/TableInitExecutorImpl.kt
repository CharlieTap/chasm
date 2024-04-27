@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal fun TableInitExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableInit,
): Result<Unit, InvocationError> =
    TableInitExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        tableSetExecutor = ::TableSetExecutorImpl,
    )

internal fun TableInitExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableInit,
    tableSetExecutor: TableSetExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val tableAddress = frame.state.module.tableAddress(instruction.tableIdx.index()).bind()
    val tableInstance = store.table(tableAddress).bind()

    val elementAddress = frame.state.module.elementAddress(instruction.elemIdx.index()).bind()
    val elementInstance = store.element(elementAddress).bind()

    val elementsToInitialise = stack.popI32().bind()
    val segmentOffset = stack.popI32().bind()
    val tableOffset = stack.popI32().bind()

    if (
        segmentOffset + elementsToInitialise > elementInstance.elements.size ||
        tableOffset + elementsToInitialise > tableInstance.elements.size
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToInitialise == 0) return@binding

    val referenceValue = elementInstance.elements.getOrNull(segmentOffset).toResultOr {
        InvocationError.ElementReferenceLookupFailed(segmentOffset)
    }.bind()

    stack.push(Stack.Entry.Value(NumberValue.I32(tableOffset)))
    stack.push(Stack.Entry.Value(referenceValue))

    tableSetExecutor(store, stack, TableInstruction.TableSet(instruction.tableIdx)).bind()

    stack.push(Stack.Entry.Value(NumberValue.I32(tableOffset + 1)))
    stack.push(Stack.Entry.Value(NumberValue.I32(segmentOffset + 1)))
    stack.push(Stack.Entry.Value(NumberValue.I32(elementsToInitialise - 1)))

    TableInitExecutorImpl(store, stack, instruction, tableSetExecutor).bind()
}
