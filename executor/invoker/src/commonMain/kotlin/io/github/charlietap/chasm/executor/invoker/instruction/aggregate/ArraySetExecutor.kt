package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popValue
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

    val (stack) = context
    val typeIndex = instruction.typeIndex
    val frame = stack.peekFrame()
    val definedType = frame.instance
        .definedType(typeIndex)
        .bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val value = stack.popValue().bind()

    val fieldIndex = stack.popI32().bind()
    val arrayReference = stack.popArrayReference().bind()

    val arrayInstance = arrayReference.instance
    val fieldValue = fieldPacker(value, arrayType.fieldType).bind()

    if (fieldIndex !in arrayInstance.fields.indices) {
        Err(InvocationError.Trap.TrapEncountered).bind()
    }

    arrayInstance.fields[fieldIndex] = fieldValue
}
