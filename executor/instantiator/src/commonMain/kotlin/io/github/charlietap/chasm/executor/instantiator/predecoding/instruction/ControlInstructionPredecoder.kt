package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.InstructionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BlockDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrIfDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnCastDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnCastFailDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnNonNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrTableDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallIndirectDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallRefDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.IfDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.NopDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallIndirectDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallRefDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.TryTableDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.UnreachableDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Block
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Br
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrIf
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrOnCast
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrOnCastFail
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrOnNonNull
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrOnNull
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrTable
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Call
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.CallIndirect
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.CallRef
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.If
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Loop
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Nop
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Return
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.ReturnCall
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.ReturnCallIndirect
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.ReturnCallRef
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Throw
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.ThrowRef
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.TryTable
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Unreachable

internal fun ControlInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    ControlInstructionPredecoder(
        context = context,
        instruction = instruction,
        instructionPredecoder = ::InstructionPredecoder,
        blockDispatcher = ::BlockDispatcher,
        brDispatcher = ::BrDispatcher,
        brIfDispatcher = ::BrIfDispatcher,
        brOnCastDispatcher = ::BrOnCastDispatcher,
        brOnCastFailDispatcher = ::BrOnCastFailDispatcher,
        brOnNonNullDispatcher = ::BrOnNonNullDispatcher,
        brOnNullDispatcher = ::BrOnNullDispatcher,
        brTableDispatcher = ::BrTableDispatcher,
        callDispatcher = ::CallDispatcher,
        callIndirectDispatcher = ::CallIndirectDispatcher,
        callRefDispatcher = ::CallRefDispatcher,
        ifDispatcher = ::IfDispatcher,
        loopDispatcher = ::LoopDispatcher,
        nopDispatcher = ::NopDispatcher,
        returnDispatcher = ::ReturnDispatcher,
        returnCallDispatcher = ::ReturnCallDispatcher,
        returnCallIndirectDispatcher = ::ReturnCallIndirectDispatcher,
        returnCallRefDispatcher = ::ReturnCallRefDispatcher,
        throwDispatcher = ::ThrowDispatcher,
        throwRefDispatcher = ::ThrowRefDispatcher,
        tryTableDispatcher = ::TryTableDispatcher,
        unreachableDispatcher = ::UnreachableDispatcher,
    )

internal inline fun ControlInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction,
    crossinline instructionPredecoder: Predecoder<Instruction, DispatchableInstruction>,
    crossinline blockDispatcher: Dispatcher<Block>,
    crossinline brDispatcher: Dispatcher<Br>,
    crossinline brIfDispatcher: Dispatcher<BrIf>,
    crossinline brOnCastDispatcher: Dispatcher<BrOnCast>,
    crossinline brOnCastFailDispatcher: Dispatcher<BrOnCastFail>,
    crossinline brOnNonNullDispatcher: Dispatcher<BrOnNonNull>,
    crossinline brOnNullDispatcher: Dispatcher<BrOnNull>,
    crossinline brTableDispatcher: Dispatcher<BrTable>,
    crossinline callDispatcher: Dispatcher<Call>,
    crossinline callIndirectDispatcher: Dispatcher<CallIndirect>,
    crossinline callRefDispatcher: Dispatcher<CallRef>,
    crossinline ifDispatcher: Dispatcher<If>,
    crossinline loopDispatcher: Dispatcher<Loop>,
    crossinline nopDispatcher: Dispatcher<Nop>,
    crossinline returnDispatcher: Dispatcher<Return>,
    crossinline returnCallDispatcher: Dispatcher<ReturnCall>,
    crossinline returnCallIndirectDispatcher: Dispatcher<ReturnCallIndirect>,
    crossinline returnCallRefDispatcher: Dispatcher<ReturnCallRef>,
    crossinline throwDispatcher: Dispatcher<Throw>,
    crossinline throwRefDispatcher: Dispatcher<ThrowRef>,
    crossinline tryTableDispatcher: Dispatcher<TryTable>,
    crossinline unreachableDispatcher: Dispatcher<Unreachable>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is ControlInstruction.Block -> blockDispatcher(
            Block(
                blockType = instruction.blockType,
                instructions = instruction.instructions.map { instructionPredecoder(context, it).bind() },
            ),
        )
        is ControlInstruction.Br -> brDispatcher(Br(instruction.labelIndex))
        is ControlInstruction.BrIf -> brIfDispatcher(BrIf(instruction.labelIndex))
        is ControlInstruction.BrOnCast -> brOnCastDispatcher(
            BrOnCast(
                labelIndex = instruction.labelIndex,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
            ),
        )
        is ControlInstruction.BrOnCastFail -> brOnCastFailDispatcher(
            BrOnCastFail(
                labelIndex = instruction.labelIndex,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
            ),
        )
        is ControlInstruction.BrOnNonNull -> brOnNonNullDispatcher(BrOnNonNull(instruction.labelIndex))
        is ControlInstruction.BrOnNull -> brOnNullDispatcher(BrOnNull(instruction.labelIndex))
        is ControlInstruction.BrTable -> brTableDispatcher(
            BrTable(
                labelIndices = instruction.labelIndices,
                defaultLabelIndex = instruction.defaultLabelIndex,
            ),
        )
        is ControlInstruction.Call -> callDispatcher(Call(instruction.functionIndex))
        is ControlInstruction.CallIndirect -> callIndirectDispatcher(
            CallIndirect(
                typeIndex = instruction.typeIndex,
                tableIndex = instruction.tableIndex,
            ),
        )
        is ControlInstruction.CallRef -> callRefDispatcher(CallRef(instruction.typeIndex))
        is ControlInstruction.If -> ifDispatcher(
            If(
                blockType = instruction.blockType,
                thenInstructions = instruction.thenInstructions.map { instructionPredecoder(context, it).bind() },
                elseInstructions = instruction.elseInstructions?.map { instructionPredecoder(context, it).bind() },
            ),
        )
        is ControlInstruction.Loop -> loopDispatcher(
            Loop(
                blockType = instruction.blockType,
                instructions = instruction.instructions.map { instructionPredecoder(context, it).bind() },
            ),
        )
        is ControlInstruction.Nop -> nopDispatcher(Nop)
        is ControlInstruction.Return -> returnDispatcher(Return)
        is ControlInstruction.ReturnCall -> returnCallDispatcher(ReturnCall(instruction.functionIndex))
        is ControlInstruction.ReturnCallIndirect -> returnCallIndirectDispatcher(
            ReturnCallIndirect(
                typeIndex = instruction.typeIndex,
                tableIndex = instruction.tableIndex,
            ),
        )
        is ControlInstruction.ReturnCallRef -> returnCallRefDispatcher(ReturnCallRef(instruction.typeIndex))
        is ControlInstruction.Throw -> throwDispatcher(Throw(instruction.tagIndex))
        is ControlInstruction.ThrowRef -> throwRefDispatcher(ThrowRef)
        is ControlInstruction.TryTable -> tryTableDispatcher(
            TryTable(
                blockType = instruction.blockType,
                handlers = instruction.handlers,
                instructions = instruction.instructions.map { instructionPredecoder(context, it).bind() },
            ),
        )
        is ControlInstruction.Unreachable -> unreachableDispatcher(Unreachable)
    }
}
