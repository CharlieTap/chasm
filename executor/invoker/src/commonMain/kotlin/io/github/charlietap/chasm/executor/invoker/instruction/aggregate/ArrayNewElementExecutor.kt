package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayNewElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewElement,
) =
    ArrayNewElementExecutor(
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
    val store = context.store

    val (typeIndex, elementIndex) = instruction
    val frame = context.cstack.peekFrame()

    val elementAddress = frame.instance
        .elementAddress(elementIndex)
    val elementInstance = store.element(elementAddress)

    val arrayLength = stack.popI32()
    val arrayStartOffsetInSegment = stack.popI32()
    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + arrayLength

    if (arrayEndOffsetInSegment > elementInstance.elements.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    elementInstance.elements
        .slice(arrayStartOffsetInSegment until arrayEndOffsetInSegment)
        .forEach { referenceValue ->
            stack.push(referenceValue)
        }

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, arrayLength.toUInt()))
}
