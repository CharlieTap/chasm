package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.OperandTransfer
import io.github.charlietap.chasm.cir.TableIndex
import io.github.charlietap.chasm.cir.TailCallOperandTransfer
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.frontend.operand.isImmediate

internal fun FunctionCompilationContext.emitCall(
    function: FunctionIndex,
    operands: List<OperandSource>,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
) {
    emit(
        ControlInstruction.Call(
            function = function,
            operands = operands.toOperandTransfer(callFrameOffset),
            callFrameOffset = callFrameOffset,
            resultDestinationSlot = resultDestinationSlot,
        ),
    )
}

internal fun FunctionCompilationContext.emitCallIndirect(
    elementIndex: OperandSource,
    operands: List<OperandSource>,
    type: TypeIndex,
    table: TableIndex,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
) {
    checkWasmCallFrame(callFrameOffset)
    val operandTransfer = operands.toOperandTransfer(callFrameOffset)
    if (elementIndex.sourceKind == OperandSourceKind.I32Immediate) {
        emit(
            ControlInstruction.CallIndirectI(
                elementIndex.sourceBits.toInt(),
                operandTransfer,
                type,
                table,
                callFrameOffset,
                resultDestinationSlot,
            ),
        )
    } else {
        emit(
            ControlInstruction.CallIndirectS(
                elementIndex.sourceBits.toInt(),
                operandTransfer,
                type,
                table,
                callFrameOffset,
                resultDestinationSlot,
            ),
        )
    }
}

internal fun FunctionCompilationContext.emitCallRef(
    functionSlot: Int,
    operands: List<OperandSource>,
    type: TypeIndex,
    callFrameOffset: Int,
    resultDestinationSlot: Int?,
) {
    checkWasmCallFrame(callFrameOffset)
    emit(
        ControlInstruction.CallRefS(
            functionSlot,
            operands.toOperandTransfer(callFrameOffset),
            type,
            callFrameOffset,
            resultDestinationSlot,
        ),
    )
}

private fun checkWasmCallFrame(
    callFrameOffset: Int,
) {
    check(callFrameOffset in 0 until CALLER_FRAME_DELTA_LIMIT) {
        "Wasm caller-frame displacement exceeds the activation-header representation"
    }
}

internal fun FunctionCompilationContext.emitReturnCall(
    function: FunctionIndex,
    operands: List<OperandSource>,
    callFrameOffset: Int,
) {
    emit(
        ControlInstruction.ReturnCall(
            function = function,
            operands = operands.toTailCallOperandTransfer(callFrameOffset),
            callFrameOffset = callFrameOffset,
            callerActivationHeaderSlot = layout.activationHeaderSlot,
        ),
    )
}

internal fun FunctionCompilationContext.emitReturnCallIndirect(
    elementIndex: OperandSource,
    operands: List<OperandSource>,
    type: TypeIndex,
    table: TableIndex,
    callFrameOffset: Int,
) {
    val operandTransfer = operands.toTailCallOperandTransfer(callFrameOffset)
    if (elementIndex.sourceKind == OperandSourceKind.I32Immediate) {
        emit(
            ControlInstruction.ReturnCallIndirectI(
                elementIndex.sourceBits.toInt(),
                operandTransfer,
                type,
                table,
                callFrameOffset,
                layout.activationHeaderSlot,
            ),
        )
    } else {
        emit(
            ControlInstruction.ReturnCallIndirectS(
                elementIndex.sourceBits.toInt(),
                operandTransfer,
                type,
                table,
                callFrameOffset,
                layout.activationHeaderSlot,
            ),
        )
    }
}

internal fun FunctionCompilationContext.emitReturnCallRef(
    functionSlot: Int,
    operands: List<OperandSource>,
    type: TypeIndex,
    callFrameOffset: Int,
) {
    emit(
        ControlInstruction.ReturnCallRefS(
            functionSlot,
            operands.toTailCallOperandTransfer(callFrameOffset),
            type,
            callFrameOffset,
            layout.activationHeaderSlot,
        ),
    )
}

internal fun FunctionCompilationContext.callFrameOffset(): Int {
    val highestReservedSlot = operands.highestReservedSlot()
    return if (frame.isTemporary(highestReservedSlot)) highestReservedSlot + 1 else frame.temporarySlotBase
}

private fun List<OperandSource>.toOperandTransfer(
    destinationSlotBase: Int,
): OperandTransfer {
    val values = LongArray(size)
    val immediates = BooleanArray(size)
    fillTransfer(values, immediates)
    return OperandTransfer(values, immediates, destinationSlotBase)
}

private fun List<OperandSource>.toTailCallOperandTransfer(
    hostDestinationSlotBase: Int,
): TailCallOperandTransfer {
    val values = LongArray(size)
    val immediates = BooleanArray(size)
    fillTransfer(values, immediates)
    return TailCallOperandTransfer(
        wasm = OperandTransfer(values, immediates, destinationSlotBase = 0),
        host = OperandTransfer(values, immediates, destinationSlotBase = hostDestinationSlotBase),
    )
}

private fun List<OperandSource>.fillTransfer(
    values: LongArray,
    immediates: BooleanArray,
) {
    for (index in indices) {
        val source = this[index]
        values[index] = source.sourceBits
        immediates[index] = source.sourceKind.isImmediate
    }
}

private const val CALLER_FRAME_DELTA_LIMIT = 1 shl 25
