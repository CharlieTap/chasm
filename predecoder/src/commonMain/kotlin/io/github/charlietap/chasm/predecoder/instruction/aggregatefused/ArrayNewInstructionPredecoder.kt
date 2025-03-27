package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.ArrayNew
import io.github.charlietap.chasm.type.ext.arrayType

internal fun ArrayNewInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayNew,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayNewInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::ArrayNewDispatcher,
    )

internal inline fun ArrayNewInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayNew,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<ArrayNew>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val definedType = context.types[instruction.typeIndex.idx]
    val arrayType = definedType.asSubType.compositeType.arrayType() ?: Err(
        InvocationError.ArrayCompositeTypeExpected,
    ).bind()
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]

    val size = loadFactory(context, instruction.size)
    val value = loadFactory(context, instruction.value)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        ArrayNew(
            size = size,
            value = value,
            destination = destination,
            rtt = rtt,
            arrayType = arrayType,
        ),
    )
}
