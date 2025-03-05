package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewFixedDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.ArrayNewFixed
import io.github.charlietap.chasm.type.ext.arrayType

internal fun ArrayNewFixedInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayNewFixed,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayNewFixedInstructionPredecoder(
        context = context,
        instruction = instruction,
        storeFactory = storeFactory,
        dispatcher = ::ArrayNewFixedDispatcher,
    )

internal inline fun ArrayNewFixedInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayNewFixed,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<ArrayNewFixed>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val definedType = context.types[instruction.typeIndex.idx]
    val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
        InvocationError.ArrayCompositeTypeExpected,
    ).bind()

    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        ArrayNewFixed(
            destination = destination,
            definedType = definedType,
            arrayType = arrayType,
            size = instruction.size,
        ),
    )
}
