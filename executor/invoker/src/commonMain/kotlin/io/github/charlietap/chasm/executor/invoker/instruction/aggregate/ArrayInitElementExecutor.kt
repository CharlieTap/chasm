package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.invoker.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayInitElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitElement,
) =
    ArrayInitElementExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal inline fun ArrayInitElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitElement,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
) {
    val stack = context.vstack
    val store = context.store

    val (typeIndex, elementIndex) = instruction
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)

    val arrayType = definedTypeExpander(definedType).arrayType()

    val elementAddress = frame.instance
        .elementAddress(elementIndex)
    val elementInstance = store.element(elementAddress)

    val elementsToCopy = stack.popI32()
    val sourceOffsetInElementSegment = stack.popI32()
    val destinationOffsetInArray = stack.popI32()
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)

    if (
        (destinationOffsetInArray + elementsToCopy > arrayInstance.fields.size) ||
        (sourceOffsetInElementSegment + elementsToCopy > elementInstance.elements.size)
    ) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    repeat(elementsToCopy) { offset ->

        val elementIndex = sourceOffsetInElementSegment + offset
        val elementValue = elementInstance.elements[elementIndex]
        val fieldIndex = destinationOffsetInArray + offset
        val fieldValue = fieldPacker(elementValue, arrayType.fieldType)

        arrayInstance.fields[fieldIndex] = fieldValue
    }
}
