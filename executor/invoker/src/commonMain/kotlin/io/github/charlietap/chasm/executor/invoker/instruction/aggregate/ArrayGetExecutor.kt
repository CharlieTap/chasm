package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.pushExecution
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayGetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGet,
) =
    ArrayGetExecutor(
        context = context,
        typeIndex = instruction.typeIndex,
        signedUnpack = true,
        fieldUnpacker = ::FieldUnpacker,
    )

internal inline fun ArrayGetExecutor(
    context: ExecutionContext,
    typeIndex: Index.TypeIndex,
    signedUnpack: Boolean,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val store = context.store
    val stack = context.vstack

    val fieldIndex = stack.popI32()
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)
    val fieldType = arrayInstance.arrayType.fieldType

    val fieldValue = arrayInstance.field(fieldIndex)
    val unpackedValue = fieldUnpacker(fieldValue, fieldType, signedUnpack)

    stack.pushExecution(unpackedValue)
}
