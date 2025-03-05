package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayLenDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.ArrayLen

internal fun ArrayLenInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayLen,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayLenInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::ArrayLenDispatcher,
    )

internal inline fun ArrayLenInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayLen,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<ArrayLen>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = loadFactory(context, instruction.address)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        ArrayLen(
            address = address,
            destination = destination,
        ),
    )
}
