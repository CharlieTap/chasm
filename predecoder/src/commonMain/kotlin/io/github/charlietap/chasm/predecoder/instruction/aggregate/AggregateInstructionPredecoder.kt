package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.AnyConvertExtern
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayCopy
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayFill
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayGet
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayGetSigned
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayGetUnsigned
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayInitData
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayInitElement
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayLen
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNew
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNewData
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNewDefault
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNewElement
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNewFixed
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArraySet
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ExternConvertAny
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.I31GetSigned
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.I31GetUnsigned
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.RefI31
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructGet
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructGetSigned
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructGetUnsigned
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructNew
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructNewDefault
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.StructSet

internal fun AggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    AggregateInstructionPredecoder(
        context = context,
        instruction = instruction,
        anyConvertExternPredecoder = ::AnyConvertExternInstructionPredecoder,
        arrayCopyPredecoder = ::ArrayCopyInstructionPredecoder,
        arrayFillPredecoder = ::ArrayFillInstructionPredecoder,
        arrayGetPredecoder = ::ArrayGetInstructionPredecoder,
        arrayGetSignedPredecoder = ::ArrayGetSignedInstructionPredecoder,
        arrayGetUnsignedPredecoder = ::ArrayGetUnsignedInstructionPredecoder,
        arrayInitDataPredecoder = ::ArrayInitDataInstructionPredecoder,
        arrayInitElementPredecoder = ::ArrayInitElementInstructionPredecoder,
        arrayLenPredecoder = ::ArrayLenInstructionPredecoder,
        arrayNewPredecoder = ::ArrayNewInstructionPredecoder,
        arrayNewDataPredecoder = ::ArrayNewDataInstructionPredecoder,
        arrayNewDefaultPredecoder = ::ArrayNewDefaultInstructionPredecoder,
        arrayNewElementPredecoder = ::ArrayNewElementInstructionPredecoder,
        arrayNewFixedPredecoder = ::ArrayNewFixedInstructionPredecoder,
        arraySetPredecoder = ::ArraySetInstructionPredecoder,
        externConvertAnyPredecoder = ::ExternConvertAnyInstructionPredecoder,
        i31GetSignedPredecoder = ::I31GetSignedInstructionPredecoder,
        i31GetUnsignedPredecoder = ::I31GetUnsignedInstructionPredecoder,
        refI31Predecoder = ::RefI31InstructionPredecoder,
        structGetPredecoder = ::StructGetInstructionPredecoder,
        structGetSignedPredecoder = ::StructGetSignedInstructionPredecoder,
        structGetUnsignedPredecoder = ::StructGetUnsignedInstructionPredecoder,
        structNewPredecoder = ::StructNewInstructionPredecoder,
        structNewDefaultPredecoder = ::StructNewDefaultInstructionPredecoder,
        structSetPredecoder = ::StructSetInstructionPredecoder,
    )

internal inline fun AggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction,
    crossinline anyConvertExternPredecoder: Predecoder<AggregateInstruction.AnyConvertExtern, DispatchableInstruction>,
    crossinline arrayCopyPredecoder: Predecoder<AggregateInstruction.ArrayCopy, DispatchableInstruction>,
    crossinline arrayFillPredecoder: Predecoder<AggregateInstruction.ArrayFill, DispatchableInstruction>,
    crossinline arrayGetPredecoder: Predecoder<AggregateInstruction.ArrayGet, DispatchableInstruction>,
    crossinline arrayGetSignedPredecoder: Predecoder<AggregateInstruction.ArrayGetSigned, DispatchableInstruction>,
    crossinline arrayGetUnsignedPredecoder: Predecoder<AggregateInstruction.ArrayGetUnsigned, DispatchableInstruction>,
    crossinline arrayInitDataPredecoder: Predecoder<AggregateInstruction.ArrayInitData, DispatchableInstruction>,
    crossinline arrayInitElementPredecoder: Predecoder<AggregateInstruction.ArrayInitElement, DispatchableInstruction>,
    crossinline arrayLenPredecoder: Predecoder<AggregateInstruction.ArrayLen, DispatchableInstruction>,
    crossinline arrayNewPredecoder: Predecoder<AggregateInstruction.ArrayNew, DispatchableInstruction>,
    crossinline arrayNewDataPredecoder: Predecoder<AggregateInstruction.ArrayNewData, DispatchableInstruction>,
    crossinline arrayNewDefaultPredecoder: Predecoder<AggregateInstruction.ArrayNewDefault, DispatchableInstruction>,
    crossinline arrayNewElementPredecoder: Predecoder<AggregateInstruction.ArrayNewElement, DispatchableInstruction>,
    crossinline arrayNewFixedPredecoder: Predecoder<AggregateInstruction.ArrayNewFixed, DispatchableInstruction>,
    crossinline arraySetPredecoder: Predecoder<AggregateInstruction.ArraySet, DispatchableInstruction>,
    crossinline externConvertAnyPredecoder: Predecoder<AggregateInstruction.ExternConvertAny, DispatchableInstruction>,
    crossinline i31GetSignedPredecoder: Predecoder<AggregateInstruction.I31GetSigned, DispatchableInstruction>,
    crossinline i31GetUnsignedPredecoder: Predecoder<AggregateInstruction.I31GetUnsigned, DispatchableInstruction>,
    crossinline refI31Predecoder: Predecoder<AggregateInstruction.RefI31, DispatchableInstruction>,
    crossinline structGetPredecoder: Predecoder<AggregateInstruction.StructGet, DispatchableInstruction>,
    crossinline structGetSignedPredecoder: Predecoder<AggregateInstruction.StructGetSigned, DispatchableInstruction>,
    crossinline structGetUnsignedPredecoder: Predecoder<AggregateInstruction.StructGetUnsigned, DispatchableInstruction>,
    crossinline structNewPredecoder: Predecoder<AggregateInstruction.StructNew, DispatchableInstruction>,
    crossinline structNewDefaultPredecoder: Predecoder<AggregateInstruction.StructNewDefault, DispatchableInstruction>,
    crossinline structSetPredecoder: Predecoder<AggregateInstruction.StructSet, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is AggregateInstruction.AnyConvertExtern -> anyConvertExternPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayCopy -> arrayCopyPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayFill -> arrayFillPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayGet -> arrayGetPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayGetSigned -> arrayGetSignedPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayGetUnsigned -> arrayGetUnsignedPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayInitData -> arrayInitDataPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayInitElement -> arrayInitElementPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayLen -> arrayLenPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayNew -> arrayNewPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayNewData -> arrayNewDataPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayNewDefault -> arrayNewDefaultPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayNewElement -> arrayNewElementPredecoder(context, instruction).bind()
        is AggregateInstruction.ArrayNewFixed -> arrayNewFixedPredecoder(context, instruction).bind()
        is AggregateInstruction.ArraySet -> arraySetPredecoder(context, instruction).bind()
        is AggregateInstruction.ExternConvertAny -> externConvertAnyPredecoder(context, instruction).bind()
        is AggregateInstruction.I31GetSigned -> i31GetSignedPredecoder(context, instruction).bind()
        is AggregateInstruction.I31GetUnsigned -> i31GetUnsignedPredecoder(context, instruction).bind()
        is AggregateInstruction.RefI31 -> refI31Predecoder(context, instruction).bind()
        is AggregateInstruction.StructGet -> structGetPredecoder(context, instruction).bind()
        is AggregateInstruction.StructGetSigned -> structGetSignedPredecoder(context, instruction).bind()
        is AggregateInstruction.StructGetUnsigned -> structGetUnsignedPredecoder(context, instruction).bind()
        is AggregateInstruction.StructNew -> structNewPredecoder(context, instruction).bind()
        is AggregateInstruction.StructNewDefault -> structNewDefaultPredecoder(context, instruction).bind()
        is AggregateInstruction.StructSet -> structSetPredecoder(context, instruction).bind()
    }
}
