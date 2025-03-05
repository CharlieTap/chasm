package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal fun FusedAggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedAggregateInstructionPredecoder(
        context = context,
        instruction = instruction,
        arrayCopyPredecoder = ::ArrayCopyInstructionPredecoder,
        arrayFillPredecoder = ::ArrayFillInstructionPredecoder,
        arrayGetPredecoder = ::ArrayGetInstructionPredecoder,
        arrayGetSignedPredecoder = ::ArrayGetSignedInstructionPredecoder,
        arrayGetUnsignedPredecoder = ::ArrayGetUnsignedInstructionPredecoder,
        arrayLenPredecoder = ::ArrayLenInstructionPredecoder,
        arrayNewPredecoder = ::ArrayNewInstructionPredecoder,
        arrayNewFixedPredecoder = ::ArrayNewFixedInstructionPredecoder,
        arraySetPredecoder = ::ArraySetInstructionPredecoder,
        structGetPredecoder = ::StructGetInstructionPredecoder,
        structGetSignedPredecoder = ::StructGetSignedInstructionPredecoder,
        structGetUnsignedPredecoder = ::StructGetUnsignedInstructionPredecoder,
        structNewPredecoder = ::StructNewInstructionPredecoder,
        structNewDefaultPredecoder = ::StructNewDefaultInstructionPredecoder,
        structSetPredecoder = ::StructSetInstructionPredecoder,
    )

internal inline fun FusedAggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction,
    crossinline arrayCopyPredecoder: Predecoder<FusedAggregateInstruction.ArrayCopy, DispatchableInstruction>,
    crossinline arrayFillPredecoder: Predecoder<FusedAggregateInstruction.ArrayFill, DispatchableInstruction>,
    crossinline arrayGetPredecoder: Predecoder<FusedAggregateInstruction.ArrayGet, DispatchableInstruction>,
    crossinline arrayGetSignedPredecoder: Predecoder<FusedAggregateInstruction.ArrayGetSigned, DispatchableInstruction>,
    crossinline arrayGetUnsignedPredecoder: Predecoder<FusedAggregateInstruction.ArrayGetUnsigned, DispatchableInstruction>,
    crossinline arrayLenPredecoder: Predecoder<FusedAggregateInstruction.ArrayLen, DispatchableInstruction>,
    crossinline arrayNewPredecoder: Predecoder<FusedAggregateInstruction.ArrayNew, DispatchableInstruction>,
    crossinline arrayNewFixedPredecoder: Predecoder<FusedAggregateInstruction.ArrayNewFixed, DispatchableInstruction>,
    crossinline arraySetPredecoder: Predecoder<FusedAggregateInstruction.ArraySet, DispatchableInstruction>,
    crossinline structGetPredecoder: Predecoder<FusedAggregateInstruction.StructGet, DispatchableInstruction>,
    crossinline structGetSignedPredecoder: Predecoder<FusedAggregateInstruction.StructGetSigned, DispatchableInstruction>,
    crossinline structGetUnsignedPredecoder: Predecoder<FusedAggregateInstruction.StructGetUnsigned, DispatchableInstruction>,
    crossinline structNewPredecoder: Predecoder<FusedAggregateInstruction.StructNew, DispatchableInstruction>,
    crossinline structNewDefaultPredecoder: Predecoder<FusedAggregateInstruction.StructNewDefault, DispatchableInstruction>,
    crossinline structSetPredecoder: Predecoder<FusedAggregateInstruction.StructSet, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedAggregateInstruction.ArrayCopy -> arrayCopyPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.ArrayFill -> arrayFillPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.ArrayGet -> arrayGetPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.ArrayGetSigned -> arrayGetSignedPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.ArrayGetUnsigned -> arrayGetUnsignedPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.ArrayLen -> arrayLenPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.ArrayNew -> arrayNewPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.ArrayNewFixed -> arrayNewFixedPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.ArraySet -> arraySetPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.StructGet -> structGetPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.StructGetSigned -> structGetSignedPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.StructGetUnsigned -> structGetUnsignedPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.StructNew -> structNewPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.StructNewDefault -> structNewDefaultPredecoder(context, instruction).bind()
        is FusedAggregateInstruction.StructSet -> structSetPredecoder(context, instruction).bind()
    }
}
