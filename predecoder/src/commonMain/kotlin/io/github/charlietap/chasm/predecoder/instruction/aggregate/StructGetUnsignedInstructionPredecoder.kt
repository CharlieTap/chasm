package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetUnsignedDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructGetUnsigned

internal fun StructGetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructGetUnsigned,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructGetUnsignedInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::StructGetUnsignedDispatcher,
    )

internal inline fun StructGetUnsignedInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructGetUnsigned,
    crossinline dispatcher: Dispatcher<StructGetUnsigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(StructGetUnsigned(instruction.typeIndex, instruction.fieldIndex))
}
