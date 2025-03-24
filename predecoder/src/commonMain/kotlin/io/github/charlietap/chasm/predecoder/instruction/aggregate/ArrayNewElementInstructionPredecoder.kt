package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewElementDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.elementAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNewElement
import io.github.charlietap.chasm.type.ext.arrayType

internal fun ArrayNewElementInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNewElement,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayNewElementInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayNewElementDispatcher,
    )

internal inline fun ArrayNewElementInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNewElement,
    crossinline dispatcher: Dispatcher<ArrayNewElement>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val elementAddress = context.instance.elementAddress(instruction.elementIndex).bind()
    val elementInstance = context.store.element(elementAddress)
    val definedType = context.types[instruction.typeIndex.idx]
    val arrayType = definedType.asSubType.compositeType.arrayType() ?: Err(
        InvocationError.ArrayCompositeTypeExpected,
    ).bind()

    dispatcher(ArrayNewElement(definedType, arrayType, elementInstance))
}
