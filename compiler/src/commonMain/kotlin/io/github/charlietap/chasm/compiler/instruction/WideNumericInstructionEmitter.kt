package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.ast.instruction.NumericOpcode
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.i64Immediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.NumericSuperInstructionDispatcher
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

internal fun FunctionCompilationContext.emitI64WideInstruction(
    opcode: NumericOpcode,
    first: OperandSource,
    second: OperandSource,
    third: OperandSource?,
    fourth: OperandSource?,
    destinationLowSlot: Int,
    destinationHighSlot: Int,
) {
    val linkedInstruction = when (opcode) {
        NumericOpcode.I64Add128 -> strictI64Quad(
            first = first,
            second = second,
            third = checkNotNull(third),
            fourth = checkNotNull(fourth),
            iiii = { operand1, operand2, operand3, operand4 ->
                NumericSuperInstruction.I64Add128Iiii(
                    operand1,
                    operand2,
                    operand3,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            iiis = { operand1, operand2, operand3, operand4Slot ->
                NumericSuperInstruction.I64Add128Iiis(
                    operand1,
                    operand2,
                    operand3,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            iisi = { operand1, operand2, operand3Slot, operand4 ->
                NumericSuperInstruction.I64Add128Iisi(
                    operand1,
                    operand2,
                    operand3Slot,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            iiss = { operand1, operand2, operand3Slot, operand4Slot ->
                NumericSuperInstruction.I64Add128Iiss(
                    operand1,
                    operand2,
                    operand3Slot,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            isii = { operand1, operand2Slot, operand3, operand4 ->
                NumericSuperInstruction.I64Add128Isii(
                    operand1,
                    operand2Slot,
                    operand3,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            isis = { operand1, operand2Slot, operand3, operand4Slot ->
                NumericSuperInstruction.I64Add128Isis(
                    operand1,
                    operand2Slot,
                    operand3,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            issi = { operand1, operand2Slot, operand3Slot, operand4 ->
                NumericSuperInstruction.I64Add128Issi(
                    operand1,
                    operand2Slot,
                    operand3Slot,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            isss = { operand1, operand2Slot, operand3Slot, operand4Slot ->
                NumericSuperInstruction.I64Add128Isss(
                    operand1,
                    operand2Slot,
                    operand3Slot,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            siii = { operand1Slot, operand2, operand3, operand4 ->
                NumericSuperInstruction.I64Add128Siii(
                    operand1Slot,
                    operand2,
                    operand3,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            siis = { operand1Slot, operand2, operand3, operand4Slot ->
                NumericSuperInstruction.I64Add128Siis(
                    operand1Slot,
                    operand2,
                    operand3,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            sisi = { operand1Slot, operand2, operand3Slot, operand4 ->
                NumericSuperInstruction.I64Add128Sisi(
                    operand1Slot,
                    operand2,
                    operand3Slot,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            siss = { operand1Slot, operand2, operand3Slot, operand4Slot ->
                NumericSuperInstruction.I64Add128Siss(
                    operand1Slot,
                    operand2,
                    operand3Slot,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            ssii = { operand1Slot, operand2Slot, operand3, operand4 ->
                NumericSuperInstruction.I64Add128Ssii(
                    operand1Slot,
                    operand2Slot,
                    operand3,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            ssis = { operand1Slot, operand2Slot, operand3, operand4Slot ->
                NumericSuperInstruction.I64Add128Ssis(
                    operand1Slot,
                    operand2Slot,
                    operand3,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            sssi = { operand1Slot, operand2Slot, operand3Slot, operand4 ->
                NumericSuperInstruction.I64Add128Sssi(
                    operand1Slot,
                    operand2Slot,
                    operand3Slot,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            ssss = { operand1Slot, operand2Slot, operand3Slot, operand4Slot ->
                NumericSuperInstruction.I64Add128Ssss(
                    operand1Slot,
                    operand2Slot,
                    operand3Slot,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
        )
        NumericOpcode.I64Sub128 -> strictI64Quad(
            first = first,
            second = second,
            third = checkNotNull(third),
            fourth = checkNotNull(fourth),
            iiii = { operand1, operand2, operand3, operand4 ->
                NumericSuperInstruction.I64Sub128Iiii(
                    operand1,
                    operand2,
                    operand3,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            iiis = { operand1, operand2, operand3, operand4Slot ->
                NumericSuperInstruction.I64Sub128Iiis(
                    operand1,
                    operand2,
                    operand3,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            iisi = { operand1, operand2, operand3Slot, operand4 ->
                NumericSuperInstruction.I64Sub128Iisi(
                    operand1,
                    operand2,
                    operand3Slot,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            iiss = { operand1, operand2, operand3Slot, operand4Slot ->
                NumericSuperInstruction.I64Sub128Iiss(
                    operand1,
                    operand2,
                    operand3Slot,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            isii = { operand1, operand2Slot, operand3, operand4 ->
                NumericSuperInstruction.I64Sub128Isii(
                    operand1,
                    operand2Slot,
                    operand3,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            isis = { operand1, operand2Slot, operand3, operand4Slot ->
                NumericSuperInstruction.I64Sub128Isis(
                    operand1,
                    operand2Slot,
                    operand3,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            issi = { operand1, operand2Slot, operand3Slot, operand4 ->
                NumericSuperInstruction.I64Sub128Issi(
                    operand1,
                    operand2Slot,
                    operand3Slot,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            isss = { operand1, operand2Slot, operand3Slot, operand4Slot ->
                NumericSuperInstruction.I64Sub128Isss(
                    operand1,
                    operand2Slot,
                    operand3Slot,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            siii = { operand1Slot, operand2, operand3, operand4 ->
                NumericSuperInstruction.I64Sub128Siii(
                    operand1Slot,
                    operand2,
                    operand3,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            siis = { operand1Slot, operand2, operand3, operand4Slot ->
                NumericSuperInstruction.I64Sub128Siis(
                    operand1Slot,
                    operand2,
                    operand3,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            sisi = { operand1Slot, operand2, operand3Slot, operand4 ->
                NumericSuperInstruction.I64Sub128Sisi(
                    operand1Slot,
                    operand2,
                    operand3Slot,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            siss = { operand1Slot, operand2, operand3Slot, operand4Slot ->
                NumericSuperInstruction.I64Sub128Siss(
                    operand1Slot,
                    operand2,
                    operand3Slot,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            ssii = { operand1Slot, operand2Slot, operand3, operand4 ->
                NumericSuperInstruction.I64Sub128Ssii(
                    operand1Slot,
                    operand2Slot,
                    operand3,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            ssis = { operand1Slot, operand2Slot, operand3, operand4Slot ->
                NumericSuperInstruction.I64Sub128Ssis(
                    operand1Slot,
                    operand2Slot,
                    operand3,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            sssi = { operand1Slot, operand2Slot, operand3Slot, operand4 ->
                NumericSuperInstruction.I64Sub128Sssi(
                    operand1Slot,
                    operand2Slot,
                    operand3Slot,
                    operand4,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
            ssss = { operand1Slot, operand2Slot, operand3Slot, operand4Slot ->
                NumericSuperInstruction.I64Sub128Ssss(
                    operand1Slot,
                    operand2Slot,
                    operand3Slot,
                    operand4Slot,
                    destinationLowSlot,
                    destinationHighSlot,
                )
            },
        )
        NumericOpcode.I64MulWideS -> strictI64BinaryDualDestination(
            left = first,
            right = second,
            ii = { left, right -> NumericSuperInstruction.I64MulWideSIi(left, right, destinationLowSlot, destinationHighSlot) },
            `is` = { left, rightSlot -> NumericSuperInstruction.I64MulWideSIs(left, rightSlot, destinationLowSlot, destinationHighSlot) },
            si = { leftSlot, right -> NumericSuperInstruction.I64MulWideSSi(leftSlot, right, destinationLowSlot, destinationHighSlot) },
            ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64MulWideSSs(leftSlot, rightSlot, destinationLowSlot, destinationHighSlot) },
        )
        NumericOpcode.I64MulWideU -> strictI64BinaryDualDestination(
            left = first,
            right = second,
            ii = { left, right -> NumericSuperInstruction.I64MulWideUIi(left, right, destinationLowSlot, destinationHighSlot) },
            `is` = { left, rightSlot -> NumericSuperInstruction.I64MulWideUIs(left, rightSlot, destinationLowSlot, destinationHighSlot) },
            si = { leftSlot, right -> NumericSuperInstruction.I64MulWideUSi(leftSlot, right, destinationLowSlot, destinationHighSlot) },
            ss = { leftSlot, rightSlot -> NumericSuperInstruction.I64MulWideUSs(leftSlot, rightSlot, destinationLowSlot, destinationHighSlot) },
        )
        else -> error("numeric opcode does not have two results: $opcode")
    }
    emit(linkedInstruction, ::NumericSuperInstructionDispatcher)
}

private inline fun strictI64Quad(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    fourth: OperandSource,
    iiii: (Long, Long, Long, Long) -> NumericSuperInstruction,
    iiis: (Long, Long, Long, Int) -> NumericSuperInstruction,
    iisi: (Long, Long, Int, Long) -> NumericSuperInstruction,
    iiss: (Long, Long, Int, Int) -> NumericSuperInstruction,
    isii: (Long, Int, Long, Long) -> NumericSuperInstruction,
    isis: (Long, Int, Long, Int) -> NumericSuperInstruction,
    issi: (Long, Int, Int, Long) -> NumericSuperInstruction,
    isss: (Long, Int, Int, Int) -> NumericSuperInstruction,
    siii: (Int, Long, Long, Long) -> NumericSuperInstruction,
    siis: (Int, Long, Long, Int) -> NumericSuperInstruction,
    sisi: (Int, Long, Int, Long) -> NumericSuperInstruction,
    siss: (Int, Long, Int, Int) -> NumericSuperInstruction,
    ssii: (Int, Int, Long, Long) -> NumericSuperInstruction,
    ssis: (Int, Int, Long, Int) -> NumericSuperInstruction,
    sssi: (Int, Int, Int, Long) -> NumericSuperInstruction,
    ssss: (Int, Int, Int, Int) -> NumericSuperInstruction,
): NumericSuperInstruction {
    val shape =
        (if (first.sourceKind == OperandSourceKind.I64Immediate) 0 else 8) or
            (if (second.sourceKind == OperandSourceKind.I64Immediate) 0 else 4) or
            (if (third.sourceKind == OperandSourceKind.I64Immediate) 0 else 2) or
            (if (fourth.sourceKind == OperandSourceKind.I64Immediate) 0 else 1)
    return when (shape) {
        0 -> iiii(first.i64Immediate(), second.i64Immediate(), third.i64Immediate(), fourth.i64Immediate())
        1 -> iiis(first.i64Immediate(), second.i64Immediate(), third.i64Immediate(), fourth.i64Slot())
        2 -> iisi(first.i64Immediate(), second.i64Immediate(), third.i64Slot(), fourth.i64Immediate())
        3 -> iiss(first.i64Immediate(), second.i64Immediate(), third.i64Slot(), fourth.i64Slot())
        4 -> isii(first.i64Immediate(), second.i64Slot(), third.i64Immediate(), fourth.i64Immediate())
        5 -> isis(first.i64Immediate(), second.i64Slot(), third.i64Immediate(), fourth.i64Slot())
        6 -> issi(first.i64Immediate(), second.i64Slot(), third.i64Slot(), fourth.i64Immediate())
        7 -> isss(first.i64Immediate(), second.i64Slot(), third.i64Slot(), fourth.i64Slot())
        8 -> siii(first.i64Slot(), second.i64Immediate(), third.i64Immediate(), fourth.i64Immediate())
        9 -> siis(first.i64Slot(), second.i64Immediate(), third.i64Immediate(), fourth.i64Slot())
        10 -> sisi(first.i64Slot(), second.i64Immediate(), third.i64Slot(), fourth.i64Immediate())
        11 -> siss(first.i64Slot(), second.i64Immediate(), third.i64Slot(), fourth.i64Slot())
        12 -> ssii(first.i64Slot(), second.i64Slot(), third.i64Immediate(), fourth.i64Immediate())
        13 -> ssis(first.i64Slot(), second.i64Slot(), third.i64Immediate(), fourth.i64Slot())
        14 -> sssi(first.i64Slot(), second.i64Slot(), third.i64Slot(), fourth.i64Immediate())
        15 -> ssss(first.i64Slot(), second.i64Slot(), third.i64Slot(), fourth.i64Slot())
        else -> error("unexpected i64 quad operand shape: $shape")
    }
}

private inline fun strictI64BinaryDualDestination(
    left: OperandSource,
    right: OperandSource,
    ii: (Long, Long) -> NumericSuperInstruction,
    `is`: (Long, Int) -> NumericSuperInstruction,
    si: (Int, Long) -> NumericSuperInstruction,
    ss: (Int, Int) -> NumericSuperInstruction,
): NumericSuperInstruction {
    return if (left.sourceKind == OperandSourceKind.I64Immediate) {
        if (right.sourceKind == OperandSourceKind.I64Immediate) {
            ii(left.i64Immediate, right.i64Immediate)
        } else {
            `is`(left.i64Immediate, right.i64Slot())
        }
    } else if (right.sourceKind == OperandSourceKind.I64Immediate) {
        si(left.i64Slot(), right.i64Immediate)
    } else {
        ss(left.i64Slot(), right.i64Slot())
    }
}

private fun OperandSource.i64Immediate(): Long = i64Immediate

private fun OperandSource.i64Slot(): Int = when (sourceKind) {
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> sourceSlot
    else -> error("operand source does not reference an i64 slot: $this")
}
