package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias ControlInstructionExecutor = (ControlInstruction, Store, Stack) -> Result<Unit, InvocationError>

internal fun ControlInstructionExecutor(
    instruction: ControlInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    ControlInstructionExecutor(
        instruction = instruction,
        store = store,
        stack = stack,
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
    instruction: ControlInstruction,
    store: Store,
    stack: Stack,
    callExecutor: CallExecutor,
    callIndirectExecutor: CallIndirectExecutor,
    returnCallExecutor: ReturnCallExecutor,
    returnCallIndirectExecutor: ReturnCallIndirectExecutor,
    callRefExecutor: CallRefExecutor,
    returnCallRefExecutor: ReturnCallRefExecutor,
    blockExecutor: BlockExecutor,
    loopExecutor: LoopExecutor,
    ifExecutor: IfExecutor,
    breakExecutor: BreakExecutor,
    brIfExecutor: BrIfExecutor,
    brTableExecutor: BrTableExecutor,
    brOnNullExecutor: BrOnNullExecutor,
    brOnNonNullExecutor: BrOnNonNullExecutor,
    brOnCastExecutor: BrOnCastExecutor,
    returnExecutor: ReturnExecutor,
    throwExecutor: ThrowExecutor,
    throwRefExecutor: ThrowRefExecutor,
    tryTableExecutor: TryTableExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is ControlInstruction.Nop -> Unit
        is ControlInstruction.Unreachable -> Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
        is ControlInstruction.Block -> blockExecutor(store, stack, instruction.blockType, instruction.instructions).bind()
        is ControlInstruction.Loop -> loopExecutor(store, stack, instruction.blockType, instruction.instructions).bind()
        is ControlInstruction.If -> ifExecutor(store, stack, instruction).bind()
        is ControlInstruction.Br -> breakExecutor(stack, instruction.labelIndex).bind()
        is ControlInstruction.BrIf -> brIfExecutor(stack, instruction).bind()
        is ControlInstruction.BrTable -> brTableExecutor(stack, instruction).bind()
        is ControlInstruction.BrOnNull -> brOnNullExecutor(stack, instruction).bind()
        is ControlInstruction.BrOnNonNull -> brOnNonNullExecutor(stack, instruction).bind()
        is ControlInstruction.Return -> returnExecutor(stack).bind()
        is ControlInstruction.Call -> callExecutor(store, stack, instruction).bind()
        is ControlInstruction.CallIndirect -> callIndirectExecutor(store, stack, instruction).bind()
        is ControlInstruction.ReturnCall -> returnCallExecutor(store, stack, instruction).bind()
        is ControlInstruction.ReturnCallIndirect -> returnCallIndirectExecutor(store, stack, instruction).bind()
        is ControlInstruction.CallRef -> callRefExecutor(store, stack).bind()
        is ControlInstruction.ReturnCallRef -> returnCallRefExecutor(store, stack).bind()
        is ControlInstruction.BrOnCast ->
            brOnCastExecutor(store, stack, instruction.labelIndex, instruction.srcReferenceType, instruction.dstReferenceType, true).bind()
        is ControlInstruction.BrOnCastFail ->
            brOnCastExecutor(store, stack, instruction.labelIndex, instruction.srcReferenceType, instruction.dstReferenceType, false).bind()
        is ControlInstruction.Throw -> throwExecutor(store, stack, instruction).bind()
        is ControlInstruction.ThrowRef -> throwRefExecutor(store, stack, instruction).bind()
        is ControlInstruction.TryTable -> tryTableExecutor(store, stack, instruction).bind()
    }
}
