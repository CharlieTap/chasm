package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.struct
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
    val store = context.store
    val stack = context.vstack
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(instruction.typeIndex)

    val structType = definedTypeExpander(definedType).structType()
    val fieldType = structType.field(instruction.fieldIndex)

    val executionValue = stack.pop()
    val structRef = stack.popStructReference()
    val structInstance = store.struct(structRef.address)
    val fieldValue = fieldPacker(executionValue, fieldType)

    structInstance.fields[instruction.fieldIndex.index()] = fieldValue
}
