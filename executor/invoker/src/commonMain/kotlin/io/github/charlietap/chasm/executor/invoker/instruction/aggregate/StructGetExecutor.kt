package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructGetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGet,
) =
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
) {

    val stack = context.vstack
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)

    val structType = definedTypeExpander(definedType).structType()
    val fieldType = structType.field(fieldIndex)

    val structInstance = stack.popStructReference()

    val fieldValue = structInstance.field(fieldIndex)
    val unpackedValue = fieldUnpacker(fieldValue, fieldType, signedUnpack)

    stack.push(unpackedValue)
}
