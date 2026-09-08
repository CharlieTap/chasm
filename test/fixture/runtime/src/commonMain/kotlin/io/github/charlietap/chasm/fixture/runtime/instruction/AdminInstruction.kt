package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.type.referenceTypeTest
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

fun adminInstruction(): AdminInstruction = endFunctionAdminInstruction()

fun endFunctionAdminInstruction(
    resultCount: Int = 0,
    activationHeaderSlot: Int = resultCount,
) = AdminInstruction.EndFunction(
    resultCount = resultCount,
    activationHeaderSlot = activationHeaderSlot,
)

fun copySlotAdminInstruction(
    sourceSlot: Int = 0,
    destinationSlot: Int = 0,
) = AdminInstruction.CopySlot(
    sourceSlot = sourceSlot,
    destinationSlot = destinationSlot,
)

fun copySlotsAdminInstruction(
    sourceSlots: List<Int> = emptyList(),
    destinationSlots: List<Int> = emptyList(),
) = AdminInstruction.CopySlots(
    sourceSlots = sourceSlots.toIntArray(),
    destinationSlots = destinationSlots.toIntArray(),
)

fun jumpAdminInstruction(
    targetIp: Int = 0,
) = AdminInstruction.Jump(
    targetIp = targetIp,
)

fun jumpCopiesAdminInstruction(
    operands: OperandTransfer = operandTransfer(),
    destinationSlotBase: Int = 0,
    targetIp: Int = 0,
) = AdminInstruction.JumpCopies(
    operands = operands,
    destinationSlotBase = destinationSlotBase,
    targetIp = targetIp,
)

fun jumpIfIAdminInstruction(
    operand: Long = 0L,
    targetIp: Int = 0,
) = AdminInstruction.JumpIfI(
    operand = operand,
    targetIp = targetIp,
)

fun jumpIfSAdminInstruction(
    operandSlot: Int = 0,
    targetIp: Int = 0,
) = AdminInstruction.JumpIfS(
    operandSlot = operandSlot,
    targetIp = targetIp,
)

fun jumpIfZeroIAdminInstruction(
    operand: Long = 0L,
    targetIp: Int = 0,
) = AdminInstruction.JumpIfZeroI(
    operand = operand,
    targetIp = targetIp,
)

fun jumpIfZeroSAdminInstruction(
    operandSlot: Int = 0,
    targetIp: Int = 0,
) = AdminInstruction.JumpIfZeroS(
    operandSlot = operandSlot,
    targetIp = targetIp,
)

fun jumpIfCopyIAdminInstruction(
    operand: Long = 0L,
    sourceSlot: Int = 0,
    destinationSlot: Int = 0,
    targetIp: Int = 0,
) = AdminInstruction.JumpIfCopyI(
    operand = operand,
    sourceSlot = sourceSlot,
    destinationSlot = destinationSlot,
    targetIp = targetIp,
)

fun jumpIfCopySAdminInstruction(
    operandSlot: Int = 0,
    sourceSlot: Int = 0,
    destinationSlot: Int = 0,
    targetIp: Int = 0,
) = AdminInstruction.JumpIfCopyS(
    operandSlot = operandSlot,
    sourceSlot = sourceSlot,
    destinationSlot = destinationSlot,
    targetIp = targetIp,
)

fun jumpIfConditionAdminInstruction(
    condition: NumericCondition = numericCondition(),
    targetIp: Int = 0,
) = AdminInstruction.JumpIfCondition(
    condition = condition,
    targetIp = targetIp,
)

fun jumpIfConditionMismatchAdminInstruction(
    condition: NumericCondition = numericCondition(),
    targetIp: Int = 0,
) = AdminInstruction.JumpIfConditionMismatch(
    condition = condition,
    targetIp = targetIp,
)

fun jumpTableSAdminInstruction(
    operandSlot: Int = 0,
    targetIps: IntArray = intArrayOf(),
) = AdminInstruction.JumpTableS(
    operandSlot = operandSlot,
    targetIps = targetIps,
)

fun jumpOnNullIAdminInstruction(
    operand: Long = 0L,
    targetIp: Int = 0,
) = AdminInstruction.JumpOnNullI(
    operand = operand,
    targetIp = targetIp,
)

fun jumpOnNullSAdminInstruction(
    operandSlot: Int = 0,
    targetIp: Int = 0,
) = AdminInstruction.JumpOnNullS(
    operandSlot = operandSlot,
    targetIp = targetIp,
)

fun jumpOnNonNullIAdminInstruction(
    operand: Long = 0L,
    targetIp: Int = 0,
) = AdminInstruction.JumpOnNonNullI(
    operand = operand,
    targetIp = targetIp,
)

fun jumpOnNonNullSAdminInstruction(
    operandSlot: Int = 0,
    targetIp: Int = 0,
) = AdminInstruction.JumpOnNonNullS(
    operandSlot = operandSlot,
    targetIp = targetIp,
)

fun jumpOnCastIAdminInstruction(
    operand: Long = 0L,
    targetIp: Int = 0,
    typeTest: ReferenceTypeTest = referenceTypeTest(),
) = AdminInstruction.JumpOnCastI(
    operand = operand,
    targetIp = targetIp,
    typeTest = typeTest,
)

fun jumpOnCastSAdminInstruction(
    operandSlot: Int = 0,
    targetIp: Int = 0,
    typeTest: ReferenceTypeTest = referenceTypeTest(),
) = AdminInstruction.JumpOnCastS(
    operandSlot = operandSlot,
    targetIp = targetIp,
    typeTest = typeTest,
)

fun jumpOnCastFailIAdminInstruction(
    operand: Long = 0L,
    targetIp: Int = 0,
    typeTest: ReferenceTypeTest = referenceTypeTest(),
) = AdminInstruction.JumpOnCastFailI(
    operand = operand,
    targetIp = targetIp,
    typeTest = typeTest,
)

fun jumpOnCastFailSAdminInstruction(
    operandSlot: Int = 0,
    targetIp: Int = 0,
    typeTest: ReferenceTypeTest = referenceTypeTest(),
) = AdminInstruction.JumpOnCastFailS(
    operandSlot = operandSlot,
    targetIp = targetIp,
    typeTest = typeTest,
)

private fun numericCondition(): NumericCondition = NumericCondition.I32Eqz(FusedOperand.I32Const(0))
