package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.type.ReferenceType

sealed interface AdminInstruction : LinkedInstruction {

    data object EndFunction : AdminInstruction

    data class CopySlots(
        val sourceSlots: List<Int>,
        val destinationSlots: List<Int>,
    ) : AdminInstruction

    data class Jump(val targetIp: Int) : AdminInstruction

    data class JumpIfI(
        val operand: Long,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfS(
        val operandSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpIfV(val targetIp: Int) : AdminInstruction

    data class JumpTableI(
        val operand: Int,
        val targetIps: IntArray,
        val defaultTargetIp: Int,
    ) : AdminInstruction

    data class JumpTableS(
        val operandSlot: Int,
        val targetIps: IntArray,
        val defaultTargetIp: Int,
    ) : AdminInstruction

    data class JumpTableV(
        val targetIps: IntArray,
        val defaultTargetIp: Int,
    ) : AdminInstruction

    data class JumpOnNullI(
        val operand: Long,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpOnNullS(
        val operandSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpOnNullV(val targetIp: Int) : AdminInstruction

    data class JumpOnNonNullI(
        val operand: Long,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpOnNonNullS(
        val operandSlot: Int,
        val targetIp: Int,
    ) : AdminInstruction

    data class JumpOnNonNullV(val targetIp: Int) : AdminInstruction

    data class JumpOnCastI(
        val operand: Long,
        val targetIp: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : AdminInstruction

    data class JumpOnCastS(
        val operandSlot: Int,
        val targetIp: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : AdminInstruction

    data class JumpOnCastV(
        val targetIp: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : AdminInstruction

    data class JumpOnCastFailI(
        val operand: Long,
        val targetIp: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : AdminInstruction

    data class JumpOnCastFailS(
        val operandSlot: Int,
        val targetIp: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : AdminInstruction

    data class JumpOnCastFailV(
        val targetIp: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : AdminInstruction

    data class PushHandler(
        val handlers: List<CatchHandler>,
        val continuationIps: IntArray,
        val payloadDestinationSlots: List<List<Int>> = [],
    ) : AdminInstruction

    data object PopHandler : AdminInstruction

    data object Pause : AdminInstruction

    data object PauseIf : AdminInstruction
}
