package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.pushExecution
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayGetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGet,
) =
    ArrayGetExecutor(
        context = context,
        typeIndex = instruction.typeIndex,
        signedUnpack = true,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldUnpacker = ::FieldUnpacker,
    )

internal inline fun ArrayGetExecutor(
    context: ExecutionContext,
    typeIndex: Index.TypeIndex,
    signedUnpack: Boolean,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val store = context.store
    val stack = context.vstack
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)

    val arrayType = definedTypeExpander(definedType).arrayType()
    val fieldType = arrayType.fieldType

    val fieldIndex = stack.popI32()
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)

    val fieldValue = arrayInstance.field(Index.FieldIndex(fieldIndex.toUInt()))
    val unpackedValue = fieldUnpacker(fieldValue, fieldType, signedUnpack)

    stack.pushExecution(unpackedValue)
}
