package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.i64Immediate
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.CallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.ReturnCallDispatcher
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TailCallOperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.type.RTT

internal fun FunctionCompilationContext.emitCall(
    function: FunctionInstance,
    operands: List<OperandSource>,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
) {
    val operandTransfer = operands.toOperandTransfer(callFrameOffset)
    when (function) {
        is FunctionInstance.WasmFunction -> {
            checkWasmCallFrame(callFrameOffset)
            val instruction = ControlSuperInstruction.WasmCall(
                strategy = function.callStrategy,
                operands = operandTransfer,
                callFrameOffset = callFrameOffset,
            )
            emit(
                CallDispatcher(instruction, resultDestinationSlot),
                instruction = { instruction },
            )
        }
        is FunctionInstance.HostFunction -> {
            val instruction = ControlSuperInstruction.HostCall(
                instance = function,
                caller = compiler.instance,
                operands = operandTransfer,
                callFrameOffset = callFrameOffset,
            )
            emit(
                CallDispatcher(instruction, resultDestinationSlot),
                instruction = { instruction },
            )
        }
    }
}

internal fun FunctionCompilationContext.emitCallIndirect(
    elementIndex: OperandSource,
    operands: List<OperandSource>,
    type: RTT,
    table: TableInstance,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
) {
    checkWasmCallFrame(callFrameOffset)
    val operandTransfer = operands.toOperandTransfer(callFrameOffset)
    if (elementIndex.sourceKind == OperandSourceKind.I32Immediate) {
        val instruction = ControlSuperInstruction.CallIndirectI(
            elementIndex.sourceBits.toInt(),
            operandTransfer,
            type,
            table,
            compiler.instance,
            callFrameOffset,
        )
        emit(CallDispatcher(instruction, resultDestinationSlot)) { instruction }
    } else {
        val instruction = ControlSuperInstruction.CallIndirectS(
            elementIndex.sourceBits.toInt(),
            operandTransfer,
            type,
            table,
            compiler.instance,
            callFrameOffset,
        )
        emit(CallDispatcher(instruction, resultDestinationSlot)) { instruction }
    }
}

internal fun FunctionCompilationContext.emitCallRef(
    functionSlot: Int,
    operands: List<OperandSource>,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
) {
    checkWasmCallFrame(callFrameOffset)
    val instruction = ControlSuperInstruction.CallRefS(
        functionSlot,
        operands.toOperandTransfer(callFrameOffset),
        compiler.instance,
        callFrameOffset,
    )
    emit(CallDispatcher(instruction, resultDestinationSlot)) { instruction }
}

private fun checkWasmCallFrame(callFrameOffset: Int) {
    check(callFrameOffset in 0 until CALLER_FRAME_DELTA_LIMIT) {
        "Wasm caller-frame displacement exceeds the activation-header representation"
    }
}

internal fun FunctionCompilationContext.emitReturnWasmCall(
    function: FunctionInstance.WasmFunction,
    operands: List<OperandSource>,
) {
    val instruction = ControlSuperInstruction.ReturnWasmCall(
        function.callStrategy,
        operands.toOperandTransfer(0),
        layout.activationHeaderSlot,
    )
    emit(instruction, ::ReturnCallDispatcher)
}

internal fun FunctionCompilationContext.emitReturnHostCall(
    function: FunctionInstance.HostFunction,
    operands: List<OperandSource>,
    callFrameOffset: Int,
) {
    val instruction = ControlSuperInstruction.ReturnHostCall(
        instance = function,
        caller = compiler.instance,
        operands = operands.toOperandTransfer(callFrameOffset),
        callFrameOffset = callFrameOffset,
        activationHeaderSlot = layout.activationHeaderSlot,
    )
    emit(instruction, ::ReturnCallDispatcher)
}

internal fun FunctionCompilationContext.emitReturnCallIndirect(
    elementIndex: OperandSource,
    operands: List<OperandSource>,
    type: RTT,
    table: TableInstance,
    callFrameOffset: Int,
) {
    val operandTransfer = operands.toTailCallOperandTransfer(callFrameOffset)
    if (elementIndex.sourceKind == OperandSourceKind.I32Immediate) {
        val instruction = ControlSuperInstruction.ReturnCallIndirectI(
            elementIndex.sourceBits.toInt(),
            operandTransfer,
            type,
            table,
            compiler.instance,
            callFrameOffset,
            layout.activationHeaderSlot,
        )
        emit(instruction, ::ReturnCallDispatcher)
    } else {
        val instruction = ControlSuperInstruction.ReturnCallIndirectS(
            elementIndex.sourceBits.toInt(),
            operandTransfer,
            type,
            table,
            compiler.instance,
            callFrameOffset,
            layout.activationHeaderSlot,
        )
        emit(instruction, ::ReturnCallDispatcher)
    }
}

internal fun FunctionCompilationContext.emitReturnCallRef(
    functionSlot: Int,
    operands: List<OperandSource>,
    callFrameOffset: Int,
) {
    val instruction = ControlSuperInstruction.ReturnCallRefS(
        functionSlot,
        operands.toTailCallOperandTransfer(callFrameOffset),
        compiler.instance,
        callFrameOffset,
        layout.activationHeaderSlot,
    )
    emit(instruction, ::ReturnCallDispatcher)
}

internal fun FunctionCompilationContext.callFrameOffset(): Int {
    val highestReservedSlot = operands.highestReservedSlot()
    return if (frame.isTemporary(highestReservedSlot)) highestReservedSlot + 1 else frame.temporarySlotBase
}

private fun List<OperandSource>.toOperandTransfer(destinationSlotBase: Int): OperandTransfer {
    val sources = Array(size) { index -> this[index].toTransferSource() }
    return selectOperandTransfer(sources, destinationSlotBase)
}

private fun List<OperandSource>.toTailCallOperandTransfer(hostDestinationSlotBase: Int): TailCallOperandTransfer {
    val sources = Array(size) { index -> this[index].toTransferSource() }
    return TailCallOperandTransfer(
        wasm = OperandTransfer(sources, destinationSlotBase = 0),
        host = OperandTransfer(sources, destinationSlotBase = hostDestinationSlotBase),
    )
}

private fun OperandSource.toTransferSource(): TransferSource = when (sourceKind) {
    OperandSourceKind.I32Immediate,
    OperandSourceKind.I64Immediate,
    OperandSourceKind.F32Immediate,
    OperandSourceKind.F64Immediate,
    -> TransferSource.Immediate(sourceBits)
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> TransferSource.Slot(sourceBits.toInt())
}

internal fun selectOperandTransfer(
    sources: Array<TransferSource>,
    destinationSlotBase: Int,
): OperandTransfer = OperandTransfer(sources, destinationSlotBase)

private const val CALLER_FRAME_DELTA_LIMIT = 1 shl 25
