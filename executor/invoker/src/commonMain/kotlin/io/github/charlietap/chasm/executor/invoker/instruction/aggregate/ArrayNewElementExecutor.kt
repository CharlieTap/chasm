package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayNewElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewElement,
): Result<Unit, InvocationError> =
    ArrayNewElementExecutor(
        context = context,
        instruction = instruction,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
    )

internal inline fun ArrayNewElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewElement,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val (typeIndex, elementIndex) = instruction
    val frame = stack.peekFrame().bind()

    val elementAddress = frame.instance
        .elementAddress(elementIndex)
        .bind()
    val elementInstance = store.element(elementAddress).bind()

    val arrayLength = stack.popI32().bind()
    val arrayStartOffsetInSegment = stack.popI32().bind()
    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + arrayLength

    if (arrayEndOffsetInSegment > elementInstance.elements.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    elementInstance.elements
        .slice(arrayStartOffsetInSegment until arrayEndOffsetInSegment)
        .forEach { referenceValue ->
            stack.pushValue(referenceValue)
        }

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, arrayLength.toUInt())).bind()
}
