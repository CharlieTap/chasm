package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructSetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructSet,
): Result<Unit, InvocationError> =
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
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module
        .definedType(instruction.typeIndex)
        .bind()

    val structType = definedTypeExpander(definedType).structType().bind()
    val fieldType = structType.field(instruction.fieldIndex).bind()

    val executionValue = stack.popValue().bind().value

    val structRef = stack.popStructReference().bind()
    val structInstance = structRef.instance

    val fieldValue = fieldPacker(executionValue, fieldType).bind()

    structInstance.fields[instruction.fieldIndex.index()] = fieldValue
}
