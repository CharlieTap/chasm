package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayFillDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.ArrayFill

internal fun ArrayFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayFill,
    loadFactory: LoadFactory = ::LoadFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayFillInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        dispatcher = ::ArrayFillDispatcher,
    )

internal inline fun ArrayFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayFill,
    crossinline loadFactory: LoadFactory,
    crossinline dispatcher: Dispatcher<ArrayFill>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val elementsToFill = loadFactory(context, instruction.elementsToFill)
    val fillValue = loadFactory(context, instruction.fillValue)
    val arrayElementOffset = loadFactory(context, instruction.arrayElementOffset)
    val address = loadFactory(context, instruction.address)

    dispatcher(
        ArrayFill(
            elementsToFill = elementsToFill,
            fillValue = fillValue,
            arrayElementOffset = arrayElementOffset,
            address = address,
            typeIndex = instruction.typeIndex,
        ),
    )
}
