package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructSetDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction.StructSet

internal fun StructSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.StructSet,
    loadFactory: LoadFactory = ::LoadFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructSetInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        dispatcher = ::StructSetDispatcher,
    )

internal inline fun StructSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction.StructSet,
    crossinline loadFactory: LoadFactory,
    crossinline dispatcher: Dispatcher<StructSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val value = loadFactory(context, instruction.value)
    val address = loadFactory(context, instruction.address)

    dispatcher(
        StructSet(
            value = value,
            address = address,
            fieldIndex = instruction.fieldIndex.idx,
        ),
    )
}
