package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewFixedDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNewFixed
import io.github.charlietap.chasm.type.ext.arrayType

internal fun ArrayNewFixedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNewFixed,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayNewFixedInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayNewFixedDispatcher,
    )

internal inline fun ArrayNewFixedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNewFixed,
    crossinline dispatcher: Dispatcher<ArrayNewFixed>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val definedType = context.types[instruction.typeIndex.idx]
    val arrayType = definedType.asSubType.compositeType.arrayType() ?: Err(
        InvocationError.ArrayCompositeTypeExpected,
    ).bind()

    dispatcher(ArrayNewFixed(definedType, arrayType, instruction.size))
}
