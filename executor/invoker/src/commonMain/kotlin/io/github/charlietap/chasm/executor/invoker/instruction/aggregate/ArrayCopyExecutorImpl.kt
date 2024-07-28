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
import io.github.charlietap.chasm.executor.runtime.store.Store
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
        destTypeIndex = destTypeIndex,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
    )

internal fun ArrayCopyExecutorImpl(
    store: Store,
    stack: Stack,
    destTypeIndex: Index.TypeIndex,
    definedTypeExpander: DefinedTypeExpander,
): Result<Unit, InvocationError> = binding {

    // x = dest
    // y = src
    val frame = stack.peekFrame().bind()
    val destDefinedType = frame.state.module.types[destTypeIndex.index()]

    val destArrayType = definedTypeExpander(destDefinedType).arrayType().bind()
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
        // forward copy
        repeat(elementsToCopy) { copyOffset ->
            val field = source.fields[sourceOffset + copyOffset]
            destination.fields[destinationOffset + copyOffset] = field
        }
    } else {
        // backward copy
        repeat(elementsToCopy) { copyOffset ->
            val field = source.fields[sourceOffset + elementsToCopy - 1 - copyOffset]
            destination.fields[destinationOffset + elementsToCopy - 1 - copyOffset] = field
        }
    }
}
