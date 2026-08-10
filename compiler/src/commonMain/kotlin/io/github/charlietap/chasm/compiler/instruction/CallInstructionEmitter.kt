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
import io.github.charlietap.chasm.type.RTT

internal fun FunctionCompilationContext.emitCall(
    function: FunctionInstance,
    resultSlotBase: Int,
    callFrameSlot: Int,
) {
    val instruction = when (function) {
        is FunctionInstance.WasmFunction -> CallDispatcher(
            ControlSuperInstruction.WasmCall(
                plan = function.callPlan,
                resultSlotBase = resultSlotBase,
                callFrameSlot = callFrameSlot,
            ),
        )
        is FunctionInstance.HostFunction -> CallDispatcher(
            ControlSuperInstruction.HostCall(
                instance = function,
                resultSlotBase = resultSlotBase,
                callFrameSlot = callFrameSlot,
            ),
        )
    }
    program.append(instruction)
}

internal fun FunctionCompilationContext.emitCallIndirect(
    elementIndex: OperandSource,
    type: RTT,
    table: TableInstance,
    resultSlotBase: Int,
    callFrameSlot: Int,
) {
    val instruction = if (elementIndex.sourceKind == OperandSourceKind.I32Immediate) {
        CallDispatcher(
            ControlSuperInstruction.CallIndirectI(
                elementIndex.sourceBits.toInt(),
                type,
                table,
                resultSlotBase,
                callFrameSlot,
            ),
        )
    } else {
        CallDispatcher(
            ControlSuperInstruction.CallIndirectS(
                elementIndex.sourceBits.toInt(),
                type,
                table,
                resultSlotBase,
                callFrameSlot,
            ),
        )
    }
    program.append(instruction)
}

internal fun FunctionCompilationContext.emitCallRef(
    functionSlot: Int,
    resultSlotBase: Int,
    callFrameSlot: Int,
) {
    program.append(
        CallDispatcher(ControlSuperInstruction.CallRefS(functionSlot, resultSlotBase, callFrameSlot)),
    )
}

internal fun FunctionCompilationContext.emitReturnCall(
    function: FunctionInstance,
    operands: List<OperandSource>,
) {
    val callOperands = operands.toCallOperands()
    val instruction = when (function) {
        is FunctionInstance.WasmFunction -> ReturnCallDispatcher(
            ControlSuperInstruction.ReturnWasmCall(function.callPlan, callOperands),
        )
        is FunctionInstance.HostFunction -> ReturnCallDispatcher(
            ControlSuperInstruction.ReturnHostCall(function, callOperands),
        )
    }
    program.append(instruction)
}

internal fun FunctionCompilationContext.emitReturnCallIndirect(
    elementIndex: OperandSource,
    operands: List<OperandSource>,
    type: RTT,
    table: TableInstance,
) {
    val callOperands = operands.toCallOperands()
    val instruction = if (elementIndex.sourceKind == OperandSourceKind.I32Immediate) {
        ReturnCallDispatcher(
            ControlSuperInstruction.ReturnCallIndirectI(elementIndex.sourceBits.toInt(), callOperands, type, table),
        )
    } else {
        ReturnCallDispatcher(
            ControlSuperInstruction.ReturnCallIndirectS(elementIndex.sourceBits.toInt(), callOperands, type, table),
        )
    }
    program.append(instruction)
}

internal fun FunctionCompilationContext.emitReturnCallRef(
    functionSlot: Int,
    operands: List<OperandSource>,
) {
    program.append(
        ReturnCallDispatcher(
            ControlSuperInstruction.ReturnCallRefS(
                functionSlot,
                operands.toCallOperands(),
            ),
        ),
    )
}

internal fun FunctionCompilationContext.callFrameSlot(): Int {
    val highestReservedSlot = operands.highestReservedSlot()
    return if (frame.isTemporary(highestReservedSlot)) highestReservedSlot + 1 else frame.temporarySlotBase
}

internal fun FunctionCompilationContext.prepareCallOperands(
    operands: List<Operand>,
    callFrameSlot: Int,
) {
    var copyCount = 0
    var firstSourceSlot = 0
    var firstDestinationSlot = 0
    for (index in operands.indices) {
        val source = operands[index]
        if (source.sourceKind == OperandSourceKind.Local || source.sourceKind == OperandSourceKind.Frame) {
            if (copyCount == 0) {
                firstSourceSlot = source.sourceBits.toInt()
                firstDestinationSlot = callFrameSlot + index
            }
            copyCount++
        }
    }
    when (copyCount) {
        0 -> Unit
        1 -> emitCopy(firstSourceSlot, firstDestinationSlot)
        else -> {
            val sourceSlots = IntArray(copyCount)
            val destinationSlots = IntArray(copyCount)
            var copyIndex = 0
            for (index in operands.indices) {
                val source = operands[index]
                if (source.sourceKind == OperandSourceKind.Local || source.sourceKind == OperandSourceKind.Frame) {
                    sourceSlots[copyIndex] = source.sourceBits.toInt()
                    destinationSlots[copyIndex] = callFrameSlot + index
                    copyIndex++
                }
            }
            emitCopies(sourceSlots, destinationSlots)
        }
    }
    for (index in operands.indices) {
        val destinationSlot = callFrameSlot + index
        val source = operands[index]
        when (source.sourceKind) {
            OperandSourceKind.I32Immediate -> emitI32Constant(source.i32Immediate, destinationSlot)
            OperandSourceKind.I64Immediate -> emitI64Constant(source.i64Immediate, destinationSlot)
            OperandSourceKind.F32Immediate -> emitF32Constant(source.sourceBits.toInt(), destinationSlot)
            OperandSourceKind.F64Immediate -> emitF64Constant(source.sourceBits, destinationSlot)
            OperandSourceKind.Local,
            OperandSourceKind.Frame,
            -> Unit
        }
    }
}

internal fun FunctionCompilationContext.prepareCallTarget(
    operand: Operand,
    overwrittenSlots: IntRange,
    stagedSlot: Int,
): OperandSource {
    val source = operand
    if (source.sourceKind != OperandSourceKind.Local && source.sourceKind != OperandSourceKind.Frame) return source
    val sourceSlot = source.sourceBits.toInt()
    if (sourceSlot !in overwrittenSlots) return source
    frame.reserve(stagedSlot)
    emitCopy(sourceSlot, stagedSlot)
    operand.materialize(stagedSlot)
    return operand
}

private fun List<OperandSource>.toCallOperands(): List<ControlSuperInstruction.CallOperand> {
    if (isEmpty()) return emptyList()
    return ArrayList<ControlSuperInstruction.CallOperand>(size).also { operands ->
        for (index in indices) {
            val operand = this[index]
            operands.add(
                when (operand.sourceKind) {
                    OperandSourceKind.I32Immediate,
                    OperandSourceKind.I64Immediate,
                    OperandSourceKind.F32Immediate,
                    OperandSourceKind.F64Immediate,
                    -> ControlSuperInstruction.CallOperand.Immediate(operand.sourceBits)
                    OperandSourceKind.Local,
                    OperandSourceKind.Frame,
                    -> ControlSuperInstruction.CallOperand.Slot(operand.sourceBits.toInt())
                },
            )
        }
    }
}
