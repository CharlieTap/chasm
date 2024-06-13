@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

internal fun StructSetExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: AggregateInstruction.StructSet,
): Result<Unit, InvocationError> =
    StructSetExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        fieldPacker = ::FieldPackerImpl,
    )

internal inline fun StructSetExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: AggregateInstruction.StructSet,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.types[instruction.typeIndex.index()]

    val structType = definedTypeExpander(definedType).structType().bind()
    val fieldType = structType.field(instruction.fieldIndex).bind()

    val executionValue = stack.popValue().bind().value

    val structRef = stack.popStructReference().bind()
    val structInstance = store.struct(structRef.address).bind()

    val fieldValue = fieldPacker(executionValue, fieldType).bind()

    structInstance.fields[instruction.fieldIndex.index()] = fieldValue
}
