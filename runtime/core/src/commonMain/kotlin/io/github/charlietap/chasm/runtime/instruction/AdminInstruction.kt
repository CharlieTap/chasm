package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

sealed interface AdminInstruction : LinkedInstruction {

    data class EndFunction(
        val resultCount: Int,
        val activationHeaderSlot: Int,
    ) : AdminInstruction

    data class CopySlot(
        val sourceSlot: Int,
        val destinationSlot: Int,
    ) : AdminInstruction

    data class CopySlots(
        val sourceSlots: IntArray,
        val destinationSlots: IntArray,
    ) : AdminInstruction

    data class Jump(val targetIp: Int) : AdminInstruction

    data class JumpCopies(
        val operands: OperandTransfer,
        val destinationSlotBase: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfI(
        val operand: Long,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfS(
        val operandSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfZeroI(
        val operand: Long,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfZeroS(
        val operandSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfCopyI(
        val operand: Long,
        val sourceSlot: Int,
        val destinationSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfCopyS(
        val operandSlot: Int,
        val sourceSlot: Int,
        val destinationSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfCondition(
        val condition: NumericCondition,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfConditionMismatch(
        val condition: NumericCondition,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpTableS(
        val operandSlot: Int,
        val targetIps: IntArray,
    ) : AdminInstruction

    data class JumpOnNullI(
        val operand: Long,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpOnNullS(
        val operandSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpOnNonNullI(
        val operand: Long,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpOnNonNullS(
        val operandSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpOnCastI(
        val operand: Long,
        val targetIp: Int,
        val typeTest: ReferenceTypeTest,
    ) : AdminInstruction

    data class JumpOnCastS(
        val operandSlot: Int,
        val targetIp: Int,
        val typeTest: ReferenceTypeTest,
    ) : AdminInstruction

    data class JumpOnCastFailI(
        val operand: Long,
        val targetIp: Int,
        val typeTest: ReferenceTypeTest,
    ) : AdminInstruction

    data class JumpOnCastFailS(
        val operandSlot: Int,
        val targetIp: Int,
        val typeTest: ReferenceTypeTest,
    ) : AdminInstruction
}
