@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.invoker.ext.popReference
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun TableFillExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableFill,
): Result<Unit, InvocationError> =
    TableFillExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        tableSetExecutor = ::TableSetExecutorImpl,
    )

internal fun TableFillExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableFill,
    tableSetExecutor: TableSetExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrameOrError().bind()
    val tableAddress = frame.state.module.tableAddress(instruction.tableIdx.index()).bind()
    val tableInstance = store.table(tableAddress).bind()

    val elementsToFill = stack.popI32().bind()
    val fillValue = stack.popReference().bind()
    val tableOffset = stack.popI32().bind()

    if (tableOffset + elementsToFill > tableInstance.elements.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToFill == 0) return@binding

    stack.push(Stack.Entry.Value(NumberValue.I32(tableOffset)))
    stack.push(Stack.Entry.Value(fillValue))

    tableSetExecutor(store, stack, TableInstruction.TableSet(instruction.tableIdx)).bind()

    stack.push(Stack.Entry.Value(NumberValue.I32(tableOffset + 1)))
    stack.push(Stack.Entry.Value(fillValue))
    stack.push(Stack.Entry.Value(NumberValue.I32(elementsToFill - 1)))

    TableFillExecutorImpl(store, stack, instruction, tableSetExecutor).bind()
}
