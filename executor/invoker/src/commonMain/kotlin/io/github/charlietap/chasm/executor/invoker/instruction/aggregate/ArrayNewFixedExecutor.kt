package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayNewFixedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
): Result<Unit, InvocationError> =
    ArrayNewFixedExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal inline fun ArrayNewFixedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val (typeIndex, size) = instruction
    val frame = stack.peekFrame().bind()
    val definedType = frame.instance
        .definedType(typeIndex)
        .bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val fields = MutableList(size.toInt()) { _ ->
        val value = stack.popValue().bind()
        fieldPacker(value, arrayType.fieldType).bind()
    }.asReversed()

    val instance = ArrayInstance(definedType, fields)
    val reference = ReferenceValue.Array(instance)

    stack.pushValue(reference)
}
