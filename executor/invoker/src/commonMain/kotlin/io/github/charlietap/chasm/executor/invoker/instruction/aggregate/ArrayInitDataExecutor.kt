package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.dataAddress
import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.ext.bitWidth

internal fun ArrayInitDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitData,
) =
    ArrayInitDataExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal fun ArrayInitDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitData,
    definedTypeExpander: DefinedTypeExpander,
    fieldPacker: FieldPacker,
) {
    val stack = context.vstack
    val store = context.store

    val (typeIndex, dataIndex) = instruction
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)
    val arrayType = definedTypeExpander(definedType).arrayType()

    val dataAddress = frame.instance.dataAddress(dataIndex)
    val dataInstance = store.data(dataAddress)

    val elementsToCopy = stack.popI32()
    val byteArrayOffset = stack.popI32()
    val arrayOffset = stack.popI32()
    val arrayInstance = stack.popArrayReference()

    val arrayElementSizeInBytes = arrayType.fieldType.bitWidth()?.let {
        it / 8
    } ?: throw InvocationException(InvocationError.UnobservableBitWidth)

    if (
        (arrayOffset + elementsToCopy > arrayInstance.fields.size) ||
        (byteArrayOffset + (elementsToCopy * arrayElementSizeInBytes) > dataInstance.bytes.size)
    ) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    repeat(elementsToCopy) { offset ->

        val srcOffsetInByteArray = byteArrayOffset + (arrayElementSizeInBytes * offset)
        val endOffsetInByteArray = srcOffsetInByteArray + arrayElementSizeInBytes

        val byteArray = dataInstance.bytes.sliceArray(srcOffsetInByteArray until endOffsetInByteArray)
        val element = arrayType.fieldType.valueFromBytes(byteArray)

        val fieldIndex = arrayOffset + offset
        val fieldValue = fieldPacker(element, arrayType.fieldType)

        arrayInstance.fields[fieldIndex] = fieldValue
    }
}
