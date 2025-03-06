package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Br
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrIf
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrOnCast
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrOnCastFail
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrOnNonNull
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrOnNull
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrTable
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.CallIndirect
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.CallRef
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Nop
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Return
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ReturnCallIndirect
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ReturnCallRef
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Throw
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ThrowRef
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.Unreachable

internal fun ControlInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    ControlInstructionPredecoder(
        context = context,
        instruction = instruction,
        blockInstructionPredecoder = ::BlockInstructionPredecoder,
        brInstructionPredecoder = ::BrInstructionPredecoder,
        brIfInstructionPredecoder = ::BrIfInstructionPredecoder,
        brOnCastInstructionPredecoder = ::BrOnCastInstructionPredecoder,
        brOnCastFailInstructionPredecoder = ::BrOnCastFailInstructionPredecoder,
        brOnNonNullInstructionPredecoder = ::BrOnNonNullInstructionPredecoder,
        brOnNullInstructionPredecoder = ::BrOnNullInstructionPredecoder,
        brTableInstructionPredecoder = ::BrTableInstructionPredecoder,
        callInstructionPredecoder = ::CallInstructionPredecoder,
        callIndirectInstructionPredecoder = ::CallIndirectInstructionPredecoder,
        callRefInstructionPredecoder = ::CallRefInstructionPredecoder,
        ifInstructionPredecoder = ::IfInstructionPredecoder,
        loopInstructionPredecoder = ::LoopInstructionPredecoder,
        nopInstructionPredecoder = ::NopInstructionPredecoder,
        returnInstructionPredecoder = ::ReturnInstructionPredecoder,
        returnCallInstructionPredecoder = ::ReturnCallInstructionPredecoder,
        returnCallIndirectInstructionPredecoder = ::ReturnCallIndirectInstructionPredecoder,
        returnCallRefInstructionPredecoder = ::ReturnCallRefInstructionPredecoder,
        throwInstructionPredecoder = ::ThrowInstructionPredecoder,
        throwRefInstructionPredecoder = ::ThrowRefInstructionPredecoder,
        tryTableInstructionPredecoder = ::TryTableInstructionPredecoder,
        unreachableInstructionPredecoder = ::UnreachableInstructionPredecoder,
    )

internal inline fun ControlInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction,
    crossinline blockInstructionPredecoder: Predecoder<ControlInstruction.Block, DispatchableInstruction>,
    crossinline brInstructionPredecoder: Predecoder<ControlInstruction.Br, DispatchableInstruction>,
    crossinline brIfInstructionPredecoder: Predecoder<ControlInstruction.BrIf, DispatchableInstruction>,
    crossinline brOnCastInstructionPredecoder: Predecoder<ControlInstruction.BrOnCast, DispatchableInstruction>,
    crossinline brOnCastFailInstructionPredecoder: Predecoder<ControlInstruction.BrOnCastFail, DispatchableInstruction>,
    crossinline brOnNonNullInstructionPredecoder: Predecoder<ControlInstruction.BrOnNonNull, DispatchableInstruction>,
    crossinline brOnNullInstructionPredecoder: Predecoder<ControlInstruction.BrOnNull, DispatchableInstruction>,
    crossinline brTableInstructionPredecoder: Predecoder<ControlInstruction.BrTable, DispatchableInstruction>,
    crossinline callInstructionPredecoder: Predecoder<ControlInstruction.Call, DispatchableInstruction>,
    crossinline callIndirectInstructionPredecoder: Predecoder<ControlInstruction.CallIndirect, DispatchableInstruction>,
    crossinline callRefInstructionPredecoder: Predecoder<ControlInstruction.CallRef, DispatchableInstruction>,
    crossinline ifInstructionPredecoder: Predecoder<ControlInstruction.If, DispatchableInstruction>,
    crossinline loopInstructionPredecoder: Predecoder<ControlInstruction.Loop, DispatchableInstruction>,
    crossinline nopInstructionPredecoder: Predecoder<ControlInstruction.Nop, DispatchableInstruction>,
    crossinline returnInstructionPredecoder: Predecoder<ControlInstruction.Return, DispatchableInstruction>,
    crossinline returnCallInstructionPredecoder: Predecoder<ControlInstruction.ReturnCall, DispatchableInstruction>,
    crossinline returnCallIndirectInstructionPredecoder: Predecoder<ControlInstruction.ReturnCallIndirect, DispatchableInstruction>,
    crossinline returnCallRefInstructionPredecoder: Predecoder<ControlInstruction.ReturnCallRef, DispatchableInstruction>,
    crossinline throwInstructionPredecoder: Predecoder<ControlInstruction.Throw, DispatchableInstruction>,
    crossinline throwRefInstructionPredecoder: Predecoder<ControlInstruction.ThrowRef, DispatchableInstruction>,
    crossinline tryTableInstructionPredecoder: Predecoder<ControlInstruction.TryTable, DispatchableInstruction>,
    crossinline unreachableInstructionPredecoder: Predecoder<ControlInstruction.Unreachable, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is ControlInstruction.Block -> blockInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Br -> brInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.BrIf -> brIfInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.BrOnCast -> brOnCastInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.BrOnCastFail -> brOnCastFailInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.BrOnNonNull -> brOnNonNullInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.BrOnNull -> brOnNullInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.BrTable -> brTableInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Call -> callInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.CallIndirect -> callIndirectInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.CallRef -> callRefInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.If -> ifInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Loop -> loopInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Nop -> nopInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Return -> returnInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ReturnCall -> returnCallInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ReturnCallIndirect -> returnCallIndirectInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ReturnCallRef -> returnCallRefInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Throw -> throwInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ThrowRef -> throwRefInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.TryTable -> tryTableInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Unreachable -> unreachableInstructionPredecoder(context, instruction).bind()
    }
}
