package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructGet

internal fun StructGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructGet,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructGetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::StructGetDispatcher,
    )

internal inline fun StructGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructGet,
    crossinline dispatcher: Dispatcher<StructGet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(StructGet(instruction.typeIndex, instruction.fieldIndex))
}
