package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructSetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructSet,
) =
    StructSetExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal inline fun StructSetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructSet,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
) {
    val (stack) = context
    val frame = stack.peekFrame()
    val definedType = frame.instance
        .definedType(instruction.typeIndex)
        .bind()

    val structType = definedTypeExpander(definedType).structType()
    val fieldType = structType.field(instruction.fieldIndex).bind()

    val executionValue = stack.popValue()
    val structInstance = stack.popStructReference()
    val fieldValue = fieldPacker(executionValue, fieldType).bind()

    structInstance.fields[instruction.fieldIndex.index()] = fieldValue
}
