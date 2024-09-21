package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
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
        returnExecutor = ::ReturnExecutor,
        throwExecutor = ::ThrowExecutor,
        throwRefExecutor = ::ThrowRefExecutor,
        tryTableExecutor = ::TryTableExecutor,
    )

internal fun ControlInstructionExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction,
    callExecutor: Executor<ControlInstruction.Call>,
    callIndirectExecutor: Executor<ControlInstruction.CallIndirect>,
    returnCallExecutor: Executor<ControlInstruction.ReturnCall>,
    returnCallIndirectExecutor: Executor<ControlInstruction.ReturnCallIndirect>,
    callRefExecutor: Executor<ControlInstruction.CallRef>,
    returnCallRefExecutor: Executor<ControlInstruction.ReturnCallRef>,
    blockExecutor: BlockExecutor,
    loopExecutor: Executor<ControlInstruction.Loop>,
    ifExecutor: Executor<ControlInstruction.If>,
    breakExecutor: BreakExecutor,
    brIfExecutor: Executor<ControlInstruction.BrIf>,
    brTableExecutor: Executor<ControlInstruction.BrTable>,
    brOnNullExecutor: Executor<ControlInstruction.BrOnNull>,
    brOnNonNullExecutor: Executor<ControlInstruction.BrOnNonNull>,
    brOnCastExecutor: BrOnCastExecutor,
    returnExecutor: Executor<ControlInstruction.Return>,
    throwExecutor: Executor<ControlInstruction.Throw>,
    throwRefExecutor: Executor<ControlInstruction.ThrowRef>,
    tryTableExecutor: Executor<ControlInstruction.TryTable>,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    when (instruction) {
        is ControlInstruction.Nop -> Unit
        is ControlInstruction.Unreachable -> Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
        is ControlInstruction.Block -> blockExecutor(store, stack, instruction.blockType, instruction.instructions).bind()
        is ControlInstruction.Loop -> loopExecutor(context, instruction).bind()
        is ControlInstruction.If -> ifExecutor(context, instruction).bind()
        is ControlInstruction.Br -> breakExecutor(stack, instruction.labelIndex).bind()
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
        is ControlInstruction.BrOnCast ->
            brOnCastExecutor(context, instruction.labelIndex, instruction.srcReferenceType, instruction.dstReferenceType, true).bind()
        is ControlInstruction.BrOnCastFail ->
            brOnCastExecutor(context, instruction.labelIndex, instruction.srcReferenceType, instruction.dstReferenceType, false).bind()
        is ControlInstruction.Throw -> throwExecutor(context, instruction).bind()
        is ControlInstruction.ThrowRef -> throwRefExecutor(context, instruction).bind()
        is ControlInstruction.TryTable -> tryTableExecutor(context, instruction).bind()
    }
}
