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
import io.github.charlietap.chasm.runtime.instruction.CopyOperand
import io.github.charlietap.chasm.runtime.instruction.OperandCopyOrder
import io.github.charlietap.chasm.runtime.instruction.OperandCopyPlan
import io.github.charlietap.chasm.type.RTT

internal fun FunctionCompilationContext.emitCall(
    function: FunctionInstance,
    operands: List<OperandSource>,
    resultSlotBase: Int,
    callFrameSlot: Int,
) {
    val operandCopyPlan = operands.toOperandCopyPlan(callFrameSlot)
    when (function) {
        is FunctionInstance.WasmFunction -> {
            val instruction = ControlSuperInstruction.WasmCall(
                plan = function.callPlan,
                operands = operandCopyPlan,
                resultSlotBase = resultSlotBase,
                callFrameSlot = callFrameSlot,
            )
            emit(instruction, ::CallDispatcher)
        }
        is FunctionInstance.HostFunction -> {
            val instruction = ControlSuperInstruction.HostCall(
                instance = function,
                operands = operandCopyPlan,
                resultSlotBase = resultSlotBase,
                callFrameSlot = callFrameSlot,
            )
            emit(instruction, ::CallDispatcher)
        }
    }
}

internal fun FunctionCompilationContext.emitCallIndirect(
    elementIndex: OperandSource,
    operands: List<OperandSource>,
    type: RTT,
    table: TableInstance,
    resultSlotBase: Int,
    callFrameSlot: Int,
) {
    val operandCopyPlan = operands.toOperandCopyPlan(callFrameSlot)
    if (elementIndex.sourceKind == OperandSourceKind.I32Immediate) {
        val instruction = ControlSuperInstruction.CallIndirectI(
            elementIndex.sourceBits.toInt(),
            operandCopyPlan,
            type,
            table,
            resultSlotBase,
            callFrameSlot,
        )
        emit(instruction, ::CallDispatcher)
    } else {
        val instruction = ControlSuperInstruction.CallIndirectS(
            elementIndex.sourceBits.toInt(),
            operandCopyPlan,
            type,
            table,
            resultSlotBase,
            callFrameSlot,
        )
        emit(instruction, ::CallDispatcher)
    }
}

internal fun FunctionCompilationContext.emitCallRef(
    functionSlot: Int,
    operands: List<OperandSource>,
    resultSlotBase: Int,
    callFrameSlot: Int,
) {
    val instruction = ControlSuperInstruction.CallRefS(
        functionSlot,
        operands.toOperandCopyPlan(callFrameSlot),
        resultSlotBase,
        callFrameSlot,
    )
    emit(instruction, ::CallDispatcher)
}

internal fun FunctionCompilationContext.emitReturnCall(
    function: FunctionInstance,
    operands: List<OperandSource>,
) {
    val copyOperands = operands.toCopyOperands()
    when (function) {
        is FunctionInstance.WasmFunction -> {
            val instruction = ControlSuperInstruction.ReturnWasmCall(function.callPlan, copyOperands)
            emit(instruction, ::ReturnCallDispatcher)
        }
        is FunctionInstance.HostFunction -> {
            val instruction = ControlSuperInstruction.ReturnHostCall(function, copyOperands)
            emit(instruction, ::ReturnCallDispatcher)
        }
    }
}

internal fun FunctionCompilationContext.emitReturnCallIndirect(
    elementIndex: OperandSource,
    operands: List<OperandSource>,
    type: RTT,
    table: TableInstance,
) {
    val copyOperands = operands.toCopyOperands()
    if (elementIndex.sourceKind == OperandSourceKind.I32Immediate) {
        val instruction = ControlSuperInstruction.ReturnCallIndirectI(
            elementIndex.sourceBits.toInt(),
            copyOperands,
            type,
            table,
        )
        emit(instruction, ::ReturnCallDispatcher)
    } else {
        val instruction = ControlSuperInstruction.ReturnCallIndirectS(
            elementIndex.sourceBits.toInt(),
            copyOperands,
            type,
            table,
        )
        emit(instruction, ::ReturnCallDispatcher)
    }
}

internal fun FunctionCompilationContext.emitReturnCallRef(
    functionSlot: Int,
    operands: List<OperandSource>,
) {
    val instruction = ControlSuperInstruction.ReturnCallRefS(
        functionSlot,
        operands.toCopyOperands(),
    )
    emit(instruction, ::ReturnCallDispatcher)
}

internal fun FunctionCompilationContext.callFrameSlot(): Int {
    val highestReservedSlot = operands.highestReservedSlot()
    return if (frame.isTemporary(highestReservedSlot)) highestReservedSlot + 1 else frame.temporarySlotBase
}

private fun List<OperandSource>.toCopyOperands(): List<CopyOperand> {
    if (isEmpty()) return emptyList()
    return ArrayList<CopyOperand>(size).also { operands ->
        for (index in indices) {
            operands.add(this[index].toCopyOperand())
        }
    }
}

private fun List<OperandSource>.toOperandCopyPlan(destinationSlotBase: Int): OperandCopyPlan {
    val operands = Array(size) { index -> this[index].toCopyOperand() }
    return operandCopyPlan(operands, destinationSlotBase)
}

private fun OperandSource.toCopyOperand(): CopyOperand = when (sourceKind) {
    OperandSourceKind.I32Immediate,
    OperandSourceKind.I64Immediate,
    OperandSourceKind.F32Immediate,
    OperandSourceKind.F64Immediate,
    -> CopyOperand.Immediate(sourceBits)
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> CopyOperand.Slot(sourceBits.toInt())
}

internal fun operandCopyPlan(
    operands: Array<CopyOperand>,
    destinationSlotBase: Int,
): OperandCopyPlan = OperandCopyPlan(
    operands = operands,
    order = operandCopyOrder(operands, destinationSlotBase),
)

private fun operandCopyOrder(
    operands: Array<CopyOperand>,
    destinationSlotBase: Int,
): OperandCopyOrder {
    var index = 0
    while (index < operands.size) {
        val operand = operands[index]
        if (operand !is CopyOperand.Slot || operand.slot != destinationSlotBase + index) break
        index++
    }
    if (index == operands.size) return OperandCopyOrder.None

    index = 0
    while (index < operands.size) {
        val destinationSlot = destinationSlotBase + index
        var remainingIndex = index + 1
        while (remainingIndex < operands.size) {
            val remaining = operands[remainingIndex]
            if (remaining is CopyOperand.Slot && remaining.slot == destinationSlot) break
            remainingIndex++
        }
        if (remainingIndex < operands.size) break
        index++
    }
    if (index == operands.size) return OperandCopyOrder.Forward

    index = operands.lastIndex
    while (index >= 0) {
        val destinationSlot = destinationSlotBase + index
        var remainingIndex = index - 1
        while (remainingIndex >= 0) {
            val remaining = operands[remainingIndex]
            if (remaining is CopyOperand.Slot && remaining.slot == destinationSlot) break
            remainingIndex--
        }
        if (remainingIndex >= 0) break
        index--
    }
    return if (index < 0) {
        OperandCopyOrder.Reverse
    } else {
        OperandCopyOrder.Staged
    }
}
