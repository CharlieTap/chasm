package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArraySetDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.ArraySet

internal fun ArraySetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArraySet,
    loadFactory: LoadFactory = ::LoadFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArraySetInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        dispatcher = ::ArraySetDispatcher,
    )

internal inline fun ArraySetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArraySet,
    crossinline loadFactory: LoadFactory,
    crossinline dispatcher: Dispatcher<ArraySet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val value = loadFactory(context, instruction.value)
    val field = loadFactory(context, instruction.field)
    val address = loadFactory(context, instruction.address)

    dispatcher(
        ArraySet(
            value = value,
            field = field,
            address = address,
            typeIndex = instruction.typeIndex,
        ),
    )
}
