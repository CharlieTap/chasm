@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructGetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGet,
): Result<Unit, InvocationError> =
    StructGetExecutor(
        context = context,
        typeIndex = instruction.typeIndex,
        fieldIndex = instruction.fieldIndex,
        signedUnpack = true,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldUnpacker = ::FieldUnpacker,
    )

internal inline fun StructGetExecutor(
    context: ExecutionContext,
    typeIndex: Index.TypeIndex,
    fieldIndex: Index.FieldIndex,
    signedUnpack: Boolean,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldUnpacker: FieldUnpacker,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()

    val structType = definedTypeExpander(definedType).structType().bind()
    val fieldType = structType.field(fieldIndex).bind()

    val structRef = stack.popStructReference().bind()
    val structInstance = structRef.instance

    val fieldValue = structInstance.field(fieldIndex).bind()
    val unpackedValue = fieldUnpacker(fieldValue, fieldType, signedUnpack).bind()

    stack.pushValue(unpackedValue)
}
