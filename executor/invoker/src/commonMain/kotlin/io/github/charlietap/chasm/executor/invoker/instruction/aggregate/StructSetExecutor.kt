package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun StructSetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructSet,
) =
    StructSetExecutor(
        context = context,
        instruction = instruction,
        fieldPacker = ::FieldPacker,
    )

internal inline fun StructSetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructSet,
    crossinline fieldPacker: FieldPacker,
) {
    val store = context.store
    val stack = context.vstack

    val executionValue = stack.pop()
    val structRef = stack.popStructReference()

    val structInstance = store.struct(structRef.address)

    val fieldType = structInstance.structType.field(instruction.fieldIndex)
    val fieldValue = fieldPacker(executionValue, fieldType)

    structInstance.fields[instruction.fieldIndex.index()] = fieldValue
}
