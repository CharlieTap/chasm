package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNew,
) =
    StructNewExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal inline fun StructNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNew,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
) {

    val (stack) = context
    val frame = stack.peekFrame().bind()
    val definedType = frame.instance
        .definedType(instruction.typeIndex)
        .bind()

    val structType = definedTypeExpander(definedType).structType().bind()

    val fields = structType.fields
        .asReversed()
        .map { fieldType ->
            val value = stack.popValue().bind()
            fieldPacker(value, fieldType).bind()
        }.asReversed()

    val instance = StructInstance(definedType, fields.toMutableList())
    val reference = ReferenceValue.Struct(instance)

    stack.pushValue(reference)
}
