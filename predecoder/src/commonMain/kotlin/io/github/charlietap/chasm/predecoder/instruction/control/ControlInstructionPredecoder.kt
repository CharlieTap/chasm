package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal fun ControlInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    ControlInstructionPredecoder(
        context = context,
        instruction = instruction,
        callInstructionPredecoder = ::CallInstructionPredecoder,
        callIndirectInstructionPredecoder = ::CallIndirectInstructionPredecoder,
        callRefInstructionPredecoder = ::CallRefInstructionPredecoder,
        nopInstructionPredecoder = ::NopInstructionPredecoder,
        returnFunctionInstructionPredecoder = ::ReturnExpressionInstructionPredecoder,
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
    crossinline callInstructionPredecoder: Predecoder<ControlInstruction.Call, DispatchableInstruction>,
    crossinline callIndirectInstructionPredecoder: Predecoder<ControlInstruction.CallIndirect, DispatchableInstruction>,
    crossinline callRefInstructionPredecoder: Predecoder<ControlInstruction.CallRef, DispatchableInstruction>,
    crossinline nopInstructionPredecoder: Predecoder<ControlInstruction.Nop, DispatchableInstruction>,
    crossinline returnFunctionInstructionPredecoder: Predecoder<ControlInstruction.ReturnExpression, DispatchableInstruction>,
    crossinline returnCallInstructionPredecoder: Predecoder<ControlInstruction.ReturnCall, DispatchableInstruction>,
    crossinline returnCallIndirectInstructionPredecoder: Predecoder<ControlInstruction.ReturnCallIndirect, DispatchableInstruction>,
    crossinline returnCallRefInstructionPredecoder: Predecoder<ControlInstruction.ReturnCallRef, DispatchableInstruction>,
    crossinline throwInstructionPredecoder: Predecoder<ControlInstruction.Throw, DispatchableInstruction>,
    crossinline throwRefInstructionPredecoder: Predecoder<ControlInstruction.ThrowRef, DispatchableInstruction>,
    crossinline tryTableInstructionPredecoder: Predecoder<ControlInstruction.TryTable, DispatchableInstruction>,
    crossinline unreachableInstructionPredecoder: Predecoder<ControlInstruction.Unreachable, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is ControlInstruction.Call -> callInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.CallIndirect -> callIndirectInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.CallRef -> callRefInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Nop -> nopInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Return -> throw Exception("Attempt to predecode nested control flow instruction")
        is ControlInstruction.ReturnExpression -> returnFunctionInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ReturnCall -> returnCallInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ReturnCallIndirect -> returnCallIndirectInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ReturnCallRef -> returnCallRefInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Throw -> throwInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ThrowRef -> throwRefInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.TryTable -> tryTableInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Unreachable -> unreachableInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Block,
        is ControlInstruction.Br,
        is ControlInstruction.BrIf,
        is ControlInstruction.BrOnCast,
        is ControlInstruction.BrOnCastFail,
        is ControlInstruction.BrOnNonNull,
        is ControlInstruction.BrOnNull,
        is ControlInstruction.BrTable,
        is ControlInstruction.If,
        is ControlInstruction.Loop,
        -> throw Exception("Attempt to predecode nested control flow instruction $instruction")
    }
}
