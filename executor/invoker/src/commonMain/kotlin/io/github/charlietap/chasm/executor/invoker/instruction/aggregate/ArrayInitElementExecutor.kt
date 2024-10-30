@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayInitElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitElement,
): Result<Unit, InvocationError> =
    ArrayInitElementExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        arraySetExecutor = ::ArraySetExecutor,
    )

internal fun ArrayInitElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitElement,
    definedTypeExpander: DefinedTypeExpander,
    arraySetExecutor: Executor<AggregateInstruction.ArraySet>,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val (typeIndex, elementIndex) = instruction
    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()

    definedTypeExpander(definedType).arrayType().bind()

    val elementAddress = frame.state.module.elementAddress(elementIndex).bind()
    val elementInstance = store.element(elementAddress).bind()

    val elementsToCopy = stack.popI32().bind()
    val sourceOffsetInElementSegment = stack.popI32().bind()
    val destinationOffsetInArray = stack.popI32().bind()

    val arrayReference = stack.popArrayReference().bind()
    val arrayInstance = arrayReference.instance

    if (
        (destinationOffsetInArray + elementsToCopy > arrayInstance.fields.size) ||
        (sourceOffsetInElementSegment + elementsToCopy > elementInstance.elements.size)
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToCopy == 0) return@binding

    val element = elementInstance.elements[sourceOffsetInElementSegment]

    stack.pushValue(arrayReference)
    stack.pushValue(NumberValue.I32(destinationOffsetInArray))
    stack.pushValue(element)

    arraySetExecutor(context, AggregateInstruction.ArraySet(typeIndex)).bind()

    stack.pushValue(arrayReference)

    stack.pushValue(NumberValue.I32(destinationOffsetInArray + 1))
    stack.pushValue(NumberValue.I32(sourceOffsetInElementSegment + 1))
    stack.pushValue(NumberValue.I32(elementsToCopy - 1))

    ArrayInitElementExecutor(context, instruction, definedTypeExpander, arraySetExecutor).bind()
}
