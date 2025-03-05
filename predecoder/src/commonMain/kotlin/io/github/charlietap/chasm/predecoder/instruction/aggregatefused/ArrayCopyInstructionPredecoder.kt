package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayCopyDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.ArrayCopy

internal fun ArrayCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayCopy,
    loadFactory: LoadFactory = ::LoadFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayCopyInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        dispatcher = ::ArrayCopyDispatcher,
    )

internal inline fun ArrayCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.ArrayCopy,
    crossinline loadFactory: LoadFactory,
    crossinline dispatcher: Dispatcher<ArrayCopy>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val elementsToCopy = loadFactory(context, instruction.elementsToCopy)
    val sourceOffset = loadFactory(context, instruction.sourceOffset)
    val sourceAddress = loadFactory(context, instruction.sourceAddress)
    val destinationOffset = loadFactory(context, instruction.destinationOffset)
    val destinationAddress = loadFactory(context, instruction.destinationAddress)

    dispatcher(
        ArrayCopy(
            elementsToCopy = elementsToCopy,
            sourceOffset = sourceOffset,
            sourceAddress = sourceAddress,
            destinationOffset = destinationOffset,
            destinationAddress = destinationAddress,
            sourceTypeIndex = instruction.sourceTypeIndex,
            destinationTypeIndex = instruction.destinationTypeIndex,
        ),
    )
}
