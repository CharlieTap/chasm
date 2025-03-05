package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetUnsignedDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.StructGetUnsigned

internal fun StructGetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.StructGetUnsigned,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructGetUnsignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::StructGetUnsignedDispatcher,
    )

internal inline fun StructGetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.StructGetUnsigned,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<StructGetUnsigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = loadFactory(context, instruction.address)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        StructGetUnsigned(
            address = address,
            destination = destination,
            typeIndex = instruction.typeIndex,
            fieldIndex = instruction.fieldIndex,
        ),
    )
}
