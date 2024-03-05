package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun ControlInstructionExecutorImpl(
    instruction: ControlInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    ControlInstructionExecutorImpl(
        instruction = instruction,
        store = store,
        stack = stack,
        callExecutor = ::CallExecutorImpl,
        callIndirectExecutor = ::CallIndirectExecutorImpl,
        blockExecutor = ::BlockExecutorImpl,
        loopExecutor = ::LoopExecutorImpl,
        ifExecutor = ::IfExecutorImpl,
        breakExecutor = ::BreakExecutorImpl,
        brIfExecutor = ::BrIfExecutorImpl,
        brTableExecutor = ::BrTableExecutorImpl,
        returnExecutor = ::ReturnExecutorImpl,
    )

internal fun ControlInstructionExecutorImpl(
    instruction: ControlInstruction,
    store: Store,
    stack: Stack,
    callExecutor: CallExecutor,
    callIndirectExecutor: CallIndirectExecutor,
    blockExecutor: BlockExecutor,
    loopExecutor: LoopExecutor,
    ifExecutor: IfExecutor,
    breakExecutor: BreakExecutor,
    brIfExecutor: BrIfExecutor,
    brTableExecutor: BrTableExecutor,
    returnExecutor: ReturnExecutor,
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
        is ControlInstruction.Return -> returnExecutor(stack).bind()
        is ControlInstruction.Call -> callExecutor(store, stack, instruction).bind()
        is ControlInstruction.CallIndirect -> callIndirectExecutor(store, stack, instruction).bind()

        else -> Err(InvocationError.UnimplementedInstruction(instruction)).bind<Unit>()
    }
}
