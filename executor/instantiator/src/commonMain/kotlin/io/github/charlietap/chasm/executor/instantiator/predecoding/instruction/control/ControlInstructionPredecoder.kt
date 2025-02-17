package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.tableAddress
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrIfDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnCastDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnCastFailDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnNonNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrOnNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrTableDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallIndirectDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallRefDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.NopDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallIndirectDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallRefDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.UnreachableDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Br
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrIf
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrOnCast
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrOnCastFail
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrOnNonNull
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrOnNull
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.BrTable
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.CallIndirect
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.CallRef
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Nop
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Return
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.ReturnCallIndirect
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.ReturnCallRef
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Throw
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.ThrowRef
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.Unreachable
import io.github.charlietap.chasm.ir.instruction.ControlInstruction

internal fun ControlInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    ControlInstructionPredecoder(
        context = context,
        instruction = instruction,
        blockInstructionPredecoder = ::BlockInstructionPredecoder,
        callInstructionPredecoder = ::CallInstructionPredecoder,
        ifInstructionPredecoder = ::IfInstructionPredecoder,
        loopInstructionPredecoder = ::LoopInstructionPredecoder,
        returnCallInstructionPredecoder = ::ReturnCallInstructionPredecoder,
        tryTableInstructionPredecoder = ::TryTableInstructionPredecoder,
        brDispatcher = ::BrDispatcher,
        brIfDispatcher = ::BrIfDispatcher,
        brOnCastDispatcher = ::BrOnCastDispatcher,
        brOnCastFailDispatcher = ::BrOnCastFailDispatcher,
        brOnNonNullDispatcher = ::BrOnNonNullDispatcher,
        brOnNullDispatcher = ::BrOnNullDispatcher,
        brTableDispatcher = ::BrTableDispatcher,
        callIndirectDispatcher = ::CallIndirectDispatcher,
        callRefDispatcher = ::CallRefDispatcher,
        nopDispatcher = ::NopDispatcher,
        returnDispatcher = ::ReturnDispatcher,
        returnCallIndirectDispatcher = ::ReturnCallIndirectDispatcher,
        returnCallRefDispatcher = ::ReturnCallRefDispatcher,
        throwDispatcher = ::ThrowDispatcher,
        throwRefDispatcher = ::ThrowRefDispatcher,
        unreachableDispatcher = ::UnreachableDispatcher,
    )

internal inline fun ControlInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction,
    crossinline blockInstructionPredecoder: Predecoder<ControlInstruction.Block, DispatchableInstruction>,
    crossinline ifInstructionPredecoder: Predecoder<ControlInstruction.If, DispatchableInstruction>,
    crossinline callInstructionPredecoder: Predecoder<ControlInstruction.Call, DispatchableInstruction>,
    crossinline loopInstructionPredecoder: Predecoder<ControlInstruction.Loop, DispatchableInstruction>,
    crossinline returnCallInstructionPredecoder: Predecoder<ControlInstruction.ReturnCall, DispatchableInstruction>,
    crossinline tryTableInstructionPredecoder: Predecoder<ControlInstruction.TryTable, DispatchableInstruction>,
    crossinline brDispatcher: Dispatcher<Br>,
    crossinline brIfDispatcher: Dispatcher<BrIf>,
    crossinline brOnCastDispatcher: Dispatcher<BrOnCast>,
    crossinline brOnCastFailDispatcher: Dispatcher<BrOnCastFail>,
    crossinline brOnNonNullDispatcher: Dispatcher<BrOnNonNull>,
    crossinline brOnNullDispatcher: Dispatcher<BrOnNull>,
    crossinline brTableDispatcher: Dispatcher<BrTable>,
    crossinline callIndirectDispatcher: Dispatcher<CallIndirect>,
    crossinline callRefDispatcher: Dispatcher<CallRef>,
    crossinline nopDispatcher: Dispatcher<Nop>,
    crossinline returnDispatcher: Dispatcher<Return>,
    crossinline returnCallIndirectDispatcher: Dispatcher<ReturnCallIndirect>,
    crossinline returnCallRefDispatcher: Dispatcher<ReturnCallRef>,
    crossinline throwDispatcher: Dispatcher<Throw>,
    crossinline throwRefDispatcher: Dispatcher<ThrowRef>,
    crossinline unreachableDispatcher: Dispatcher<Unreachable>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is ControlInstruction.Block -> blockInstructionPredecoder(context, instruction).bind()
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
        is ControlInstruction.Call -> callInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.CallIndirect -> {

            val address = context.instance?.tableAddress(instruction.tableIndex)?.bind()
                ?: Err(InstantiationError.PredecodingError).bind()
            val table = context.store.table(address)

            callIndirectDispatcher(
                CallIndirect(
                    type = context.types[instruction.typeIndex.idx],
                    table = table,
                ),
            )
        }
        is ControlInstruction.CallRef -> callRefDispatcher(CallRef(instruction.typeIndex))
        is ControlInstruction.If -> ifInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Loop -> loopInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Nop -> nopDispatcher(Nop)
        is ControlInstruction.Return -> returnDispatcher(Return)
        is ControlInstruction.ReturnCall -> returnCallInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.ReturnCallIndirect -> {

            val address = context.instance?.tableAddress(instruction.tableIndex)?.bind()
                ?: Err(InstantiationError.PredecodingError).bind()
            val table = context.store.table(address)

            returnCallIndirectDispatcher(
                ReturnCallIndirect(
                    type = context.types[instruction.typeIndex.idx],
                    table = table,
                ),
            )
        }
        is ControlInstruction.ReturnCallRef -> returnCallRefDispatcher(ReturnCallRef(instruction.typeIndex))
        is ControlInstruction.Throw -> throwDispatcher(Throw(instruction.tagIndex))
        is ControlInstruction.ThrowRef -> throwRefDispatcher(ThrowRef)
        is ControlInstruction.TryTable -> tryTableInstructionPredecoder(context, instruction).bind()
        is ControlInstruction.Unreachable -> unreachableDispatcher(Unreachable)
    }
}
