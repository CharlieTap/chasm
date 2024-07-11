@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

internal fun ArrayCopyExecutorImpl(
    store: Store,
    stack: Stack,
    srcTypeIndex: Index.TypeIndex,
    destTypeIndex: Index.TypeIndex,
): Result<Unit, InvocationError> =
    ArrayCopyExecutorImpl(
        store = store,
        stack = stack,
        srcTypeIndex = srcTypeIndex,
        destTypeIndex = destTypeIndex,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        arrayGetExecutor = ::ArrayGetExecutorImpl,
        arraySetExecutor = ::ArraySetExecutorImpl,
    )

internal fun ArrayCopyExecutorImpl(
    store: Store,
    stack: Stack,
    srcTypeIndex: Index.TypeIndex,
    destTypeIndex: Index.TypeIndex,
    definedTypeExpander: DefinedTypeExpander,
    arrayGetExecutor: ArrayGetExecutor,
    arraySetExecutor: ArraySetExecutor,
): Result<Unit, InvocationError> = binding {

    // x = dest
    // y = src
    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.types[destTypeIndex.index()]

    val destArrayType = definedTypeExpander(definedType).arrayType().bind()
    if (destArrayType.fieldType.mutability != Mutability.Var) {
        Err(InvocationError.ArrayCopyOnAConstArray).bind<Unit>()
    }

    val elementsToCopy = stack.popI32().bind()
    val sourceOffset = stack.popI32().bind()

    val srcReference = stack.popArrayReference().bind()

    val destinationOffset = stack.popI32().bind()

    val destReference = stack.popArrayReference().bind()

    val source = store.array(srcReference.address).bind()
    val destination = store.array(destReference.address).bind()

    if (destinationOffset + elementsToCopy > destination.fields.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (sourceOffset + elementsToCopy > source.fields.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToCopy == 0) return@binding

    if (destinationOffset <= sourceOffset) {
        stack.pushValue(destReference)
        stack.pushValue(NumberValue.I32(destinationOffset))
        stack.pushValue(srcReference)
        stack.pushValue(NumberValue.I32(sourceOffset))

        arrayGetExecutor(store, stack, srcTypeIndex, false).bind()
        arraySetExecutor(store, stack, destTypeIndex).bind()

        stack.pushValue(destReference)
        stack.pushValue(NumberValue.I32(destinationOffset + 1))
        stack.pushValue(srcReference)
        stack.pushValue(NumberValue.I32(sourceOffset + 1))
    } else {

        stack.pushValue(destReference)
        stack.pushValue(NumberValue.I32(destinationOffset + elementsToCopy - 1))
        stack.pushValue(srcReference)
        stack.pushValue(NumberValue.I32(sourceOffset + elementsToCopy - 1))

        arrayGetExecutor(store, stack, srcTypeIndex, false).bind()
        arraySetExecutor(store, stack, destTypeIndex).bind()

        stack.pushValue(destReference)
        stack.pushValue(NumberValue.I32(destinationOffset))
        stack.pushValue(srcReference)
        stack.pushValue(NumberValue.I32(sourceOffset))
    }

    stack.pushValue(NumberValue.I32(elementsToCopy - 1))
    ArrayCopyExecutorImpl(
        store,
        stack,
        srcTypeIndex,
        destTypeIndex,
        definedTypeExpander,
        arrayGetExecutor,
        arraySetExecutor,
    ).bind()
}
