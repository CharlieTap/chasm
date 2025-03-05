package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetSignedDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.StructGetSigned

internal fun StructGetSignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.StructGetSigned,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructGetSignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::StructGetSignedDispatcher,
    )

internal inline fun StructGetSignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.StructGetSigned,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<StructGetSigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = loadFactory(context, instruction.address)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        StructGetSigned(
            address = address,
            destination = destination,
            typeIndex = instruction.typeIndex,
            fieldIndex = instruction.fieldIndex,
        ),
    )
}
