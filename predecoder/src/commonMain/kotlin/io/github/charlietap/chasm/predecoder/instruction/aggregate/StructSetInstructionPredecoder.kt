package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructSetDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructSet

internal fun StructSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructSet,
): Result<DispatchableInstruction, ModuleTrapError> =
    StructSetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::StructSetDispatcher,
    )

internal inline fun StructSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.StructSet,
    crossinline dispatcher: Dispatcher<StructSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(StructSet(instruction.fieldIndex.idx))
}
