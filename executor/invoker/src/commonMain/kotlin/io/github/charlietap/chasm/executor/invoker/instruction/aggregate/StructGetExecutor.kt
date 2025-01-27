package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.pushExecution
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun StructGetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructGet,
) =
    StructGetExecutor(
        context = context,
        typeIndex = instruction.typeIndex,
        fieldIndex = instruction.fieldIndex,
        signedUnpack = true,
        fieldUnpacker = ::FieldUnpacker,
    )

internal inline fun StructGetExecutor(
    context: ExecutionContext,
    typeIndex: Index.TypeIndex,
    fieldIndex: Index.FieldIndex,
    signedUnpack: Boolean,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val store = context.store
    val stack = context.vstack

    val structRef = stack.popStructReference()
    val structInstance = store.struct(structRef.address)
    val fieldType = structInstance.structType.field(fieldIndex)

    val fieldValue = structInstance.field(fieldIndex)
    val unpackedValue = fieldUnpacker(fieldValue, fieldType, signedUnpack)

    stack.pushExecution(unpackedValue)
}
