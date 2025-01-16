package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayFillExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayFill,
) =
    ArrayFillExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal inline fun ArrayFillExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayFill,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
) {
    val (stack) = context
    val elementsToFill = stack.popI32()
    val fillValue = stack.popValue()
    val arrayElementOffset = stack.popI32()
    val arrayInstance = stack.popArrayReference()

    if (arrayElementOffset + elementsToFill > arrayInstance.fields.size) {
        Err(InvocationError.Trap.TrapEncountered).bind()
    }

    if (elementsToFill == 0) return

    val frame = stack.peekFrame()
    val definedType = frame.instance
        .definedType(instruction.typeIndex)
        .bind()
    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    repeat(elementsToFill) { fillOffset ->

        val fieldIndex = arrayElementOffset + fillOffset
        val fieldValue = fieldPacker(fillValue, arrayType.fieldType).bind()

        arrayInstance.fields[fieldIndex] = fieldValue
    }
}
