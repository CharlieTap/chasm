package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayNewElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewElement,
) = ArrayNewElementExecutor(
    context = context,
    instruction = instruction,
    arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
)

internal inline fun ArrayNewElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewElement,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
) {
    val stack = context.vstack

    val elementInstance = instruction.elementInstance
    val elements = elementInstance.elements

    val arrayLength = stack.popI32()
    val arrayStartOffsetInSegment = stack.popI32()
    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + arrayLength

    if (arrayEndOffsetInSegment > elementInstance.elements.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    repeat(arrayLength) { offset ->
        stack.push(elements[arrayStartOffsetInSegment + offset])
    }

    arrayNewFixedExecutor(
        context,
        AggregateInstruction.ArrayNewFixed(
            definedType = instruction.definedType,
            arrayType = instruction.arrayType,
            size = arrayLength.toUInt(),
        ),
    )
}
