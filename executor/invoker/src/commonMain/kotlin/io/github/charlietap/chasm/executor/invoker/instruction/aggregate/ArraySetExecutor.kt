package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArraySetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArraySet,
) =
    ArraySetExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal inline fun ArraySetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArraySet,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
) {
    val store = context.store
    val stack = context.vstack
    val typeIndex = instruction.typeIndex
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)
    val arrayType = definedTypeExpander(definedType).arrayType()

    val value = stack.pop()
    val fieldIndex = stack.popI32()
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)

    val fieldValue = fieldPacker(value, arrayType.fieldType)

    if (fieldIndex !in arrayInstance.fields.indices) {
        throw InvocationException(InvocationError.Trap.TrapEncountered)
    }

    arrayInstance.fields[fieldIndex] = fieldValue
}
