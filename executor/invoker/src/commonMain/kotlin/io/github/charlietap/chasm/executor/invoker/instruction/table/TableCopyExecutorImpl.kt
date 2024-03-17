@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun TableCopyExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableCopy,
): Result<Unit, InvocationError> =
    TableCopyExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        tableGetExecutor = ::TableGetExecutorImpl,
        tableSetExecutor = ::TableSetExecutorImpl,
    )

internal fun TableCopyExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: TableInstruction.TableCopy,
    tableGetExecutor: TableGetExecutor,
    tableSetExecutor: TableSetExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrameOrError().bind()
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

    if (dstOffset <= srcOffset) {
        stack.push(Stack.Entry.Value(NumberValue.I32(dstOffset)))
        stack.push(Stack.Entry.Value(NumberValue.I32(srcOffset)))
        tableGetExecutor(store, stack, TableInstruction.TableGet(instruction.srcTableIdx)).bind()
        tableSetExecutor(store, stack, TableInstruction.TableSet(instruction.destTableIdx)).bind()
        stack.push(Stack.Entry.Value(NumberValue.I32(dstOffset + 1)))
        stack.push(Stack.Entry.Value(NumberValue.I32(srcOffset + 1)))
    } else {
        stack.push(Stack.Entry.Value(NumberValue.I32(dstOffset + elementsToCopy - 1)))
        stack.push(Stack.Entry.Value(NumberValue.I32(srcOffset + elementsToCopy - 1)))
        tableGetExecutor(store, stack, TableInstruction.TableGet(instruction.srcTableIdx)).bind()
        tableSetExecutor(store, stack, TableInstruction.TableSet(instruction.destTableIdx)).bind()
        stack.push(Stack.Entry.Value(NumberValue.I32(dstOffset)))
        stack.push(Stack.Entry.Value(NumberValue.I32(srcOffset)))
    }

    stack.push(Stack.Entry.Value(NumberValue.I32(elementsToCopy - 1)))

    TableCopyExecutorImpl(store, stack, instruction, tableGetExecutor, tableSetExecutor).bind()
}
