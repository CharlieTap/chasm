package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun ControlInstructionExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction,
): Result<Unit, InvocationError> =
    ControlInstructionExecutor(
        context = context,
        instruction = instruction,
        callExecutor = ::CallExecutor,
        callIndirectExecutor = ::CallIndirectExecutor,
        returnCallExecutor = ::ReturnCallExecutor,
        returnCallIndirectExecutor = ::ReturnCallIndirectExecutor,
        callRefExecutor = ::CallRefExecutor,
        returnCallRefExecutor = ::ReturnCallRefExecutor,
        blockExecutor = ::BlockExecutor,
        loopExecutor = ::LoopExecutor,
        ifExecutor = ::IfExecutor,
        breakExecutor = ::BreakExecutor,
        brIfExecutor = ::BrIfExecutor,
        brTableExecutor = ::BrTableExecutor,
        brOnNullExecutor = ::BrOnNullExecutor,
        brOnNonNullExecutor = ::BrOnNonNullExecutor,
        brOnCastExecutor = ::BrOnCastExecutor,
        brOnCastFailExecutor = ::BrOnCastFailExecutor,
        nopExecutor = ::NopExecutor,
        returnExecutor = ::ReturnExecutor,
        throwExecutor = ::ThrowExecutor,
        throwRefExecutor = ::ThrowRefExecutor,
        tryTableExecutor = ::TryTableExecutor,
        unreachableExecutor = ::UnreachableExecutor,
    )

internal inline fun ControlInstructionExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction,
    crossinline callExecutor: Executor<ControlInstruction.Call>,
    crossinline callIndirectExecutor: Executor<ControlInstruction.CallIndirect>,
    crossinline returnCallExecutor: Executor<ControlInstruction.ReturnCall>,
    crossinline returnCallIndirectExecutor: Executor<ControlInstruction.ReturnCallIndirect>,
    crossinline callRefExecutor: Executor<ControlInstruction.CallRef>,
    crossinline returnCallRefExecutor: Executor<ControlInstruction.ReturnCallRef>,
    crossinline blockExecutor: Executor<ControlInstruction.Block>,
    crossinline loopExecutor: Executor<ControlInstruction.Loop>,
    crossinline ifExecutor: Executor<ControlInstruction.If>,
    crossinline breakExecutor: Executor<ControlInstruction.Br>,
    crossinline brIfExecutor: Executor<ControlInstruction.BrIf>,
    crossinline brTableExecutor: Executor<ControlInstruction.BrTable>,
    crossinline brOnNullExecutor: Executor<ControlInstruction.BrOnNull>,
    crossinline brOnNonNullExecutor: Executor<ControlInstruction.BrOnNonNull>,
    crossinline brOnCastExecutor: Executor<ControlInstruction.BrOnCast>,
    crossinline brOnCastFailExecutor: Executor<ControlInstruction.BrOnCastFail>,
    crossinline nopExecutor: Executor<ControlInstruction.Nop>,
    crossinline returnExecutor: Executor<ControlInstruction.Return>,
    crossinline throwExecutor: Executor<ControlInstruction.Throw>,
    crossinline throwRefExecutor: Executor<ControlInstruction.ThrowRef>,
    crossinline tryTableExecutor: Executor<ControlInstruction.TryTable>,
    crossinline unreachableExecutor: Executor<ControlInstruction.Unreachable>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is ControlInstruction.Nop -> nopExecutor(context, instruction).bind()
        is ControlInstruction.Unreachable -> unreachableExecutor(context, instruction).bind()
        is ControlInstruction.Block -> blockExecutor(context, instruction).bind()
        is ControlInstruction.Loop -> loopExecutor(context, instruction).bind()
        is ControlInstruction.If -> ifExecutor(context, instruction).bind()
        is ControlInstruction.Br -> breakExecutor(context, instruction).bind()
        is ControlInstruction.BrIf -> brIfExecutor(context, instruction).bind()
        is ControlInstruction.BrTable -> brTableExecutor(context, instruction).bind()
        is ControlInstruction.BrOnNull -> brOnNullExecutor(context, instruction).bind()
        is ControlInstruction.BrOnNonNull -> brOnNonNullExecutor(context, instruction).bind()
        is ControlInstruction.Return -> returnExecutor(context, instruction).bind()
        is ControlInstruction.Call -> callExecutor(context, instruction).bind()
        is ControlInstruction.CallIndirect -> callIndirectExecutor(context, instruction).bind()
        is ControlInstruction.ReturnCall -> returnCallExecutor(context, instruction).bind()
        is ControlInstruction.ReturnCallIndirect -> returnCallIndirectExecutor(context, instruction).bind()
        is ControlInstruction.CallRef -> callRefExecutor(context, instruction).bind()
        is ControlInstruction.ReturnCallRef -> returnCallRefExecutor(context, instruction).bind()
        is ControlInstruction.BrOnCast -> brOnCastExecutor(context, instruction).bind()
        is ControlInstruction.BrOnCastFail -> brOnCastFailExecutor(context, instruction).bind()
        is ControlInstruction.Throw -> throwExecutor(context, instruction).bind()
        is ControlInstruction.ThrowRef -> throwRefExecutor(context, instruction).bind()
        is ControlInstruction.TryTable -> tryTableExecutor(context, instruction).bind()
    }
}
