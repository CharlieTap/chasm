package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.elementAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayInitElement

internal fun ArrayInitElementInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayInitElement,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayInitElementInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayInitElementDispatcher,
    )

internal inline fun ArrayInitElementInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayInitElement,
    crossinline dispatcher: Dispatcher<ArrayInitElement>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val elementAddress = context.instance.elementAddress(instruction.elementIndex).bind()
    val elementInstance = context.store.element(elementAddress)

    dispatcher(ArrayInitElement(instruction.typeIndex, elementInstance))
}
