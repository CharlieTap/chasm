package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetUnsignedDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.ArrayGetUnsigned

internal fun ArrayGetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayGetUnsigned,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayGetUnsignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::ArrayGetUnsignedDispatcher,
    )

internal inline fun ArrayGetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayGetUnsigned,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<ArrayGetUnsigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val field = loadFactory(context, instruction.field)
    val address = loadFactory(context, instruction.address)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        ArrayGetUnsigned(
            field = field,
            address = address,
            destination = destination,
            typeIndex = instruction.typeIndex,
        ),
    )
}
