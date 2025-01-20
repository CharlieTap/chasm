package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
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

    val (stack, store) = context
    val (typeIndex, elementIndex) = instruction
    val frame = stack.peekFrame()
    val definedType = frame.instance
        .definedType(typeIndex)
        .bind()
    val arrayType = definedTypeExpander(definedType).arrayType()

    val elementAddress = frame.instance
        .elementAddress(elementIndex)
        .bind()
    val elementInstance = store.element(elementAddress)

    val elementsToCopy = stack.popI32()
    val sourceOffsetInElementSegment = stack.popI32()
    val destinationOffsetInArray = stack.popI32()
    val arrayInstance = stack.popArrayReference()

    if (
        (destinationOffsetInArray + elementsToCopy > arrayInstance.fields.size) ||
        (sourceOffsetInElementSegment + elementsToCopy > elementInstance.elements.size)
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind()
    }

    repeat(elementsToCopy) { offset ->

        val elementIndex = sourceOffsetInElementSegment + offset
        val elementValue = elementInstance.elements[elementIndex]
        val fieldIndex = destinationOffsetInArray + offset
        val fieldValue = fieldPacker(elementValue, arrayType.fieldType)

        arrayInstance.fields[fieldIndex] = fieldValue
    }
}
