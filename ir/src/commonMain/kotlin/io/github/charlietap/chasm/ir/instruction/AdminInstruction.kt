package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.type.ReferenceType

sealed interface AdminInstruction : Instruction {

    data object EndBlock : AdminInstruction

    data object EndFunction : AdminInstruction

    data class CopySlots(
        val sourceSlots: List<Int>,
        val destinationSlots: List<Int>,
    ) : AdminInstruction

    data class Jump(
        val offset: Int,
        val adjustment: StackAdjustment = StackAdjustment(depth = 0, keep = 0),
    ) : AdminInstruction

    data class JumpIf(
        val operand: FusedOperand,
        val offset: Int,
        val takenInstructions: List<Instruction> = emptyList(),
        val adjustment: StackAdjustment = StackAdjustment(depth = 0, keep = 0),
    ) : AdminInstruction

    data class JumpTable(
        val operand: FusedOperand,
        val offsets: List<Int>,
        val defaultOffset: Int,
        val takenInstructions: List<List<Instruction>> = emptyList(),
        val defaultTakenInstructions: List<Instruction> = emptyList(),
        val adjustments: List<StackAdjustment> = emptyList(),
    ) : AdminInstruction

    data class JumpOnNull(
        val operand: FusedOperand,
        val offset: Int,
        val takenInstructions: List<Instruction> = emptyList(),
        val adjustment: StackAdjustment = StackAdjustment(depth = 0, keep = 0),
    ) : AdminInstruction

    data class JumpOnNonNull(
        val operand: FusedOperand,
        val offset: Int,
        val takenInstructions: List<Instruction> = emptyList(),
        val adjustment: StackAdjustment = StackAdjustment(depth = 0, keep = 0),
    ) : AdminInstruction

    data class JumpOnCast(
        val operand: FusedOperand,
        val offset: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<Instruction> = emptyList(),
        val adjustment: StackAdjustment = StackAdjustment(depth = 0, keep = 0),
    ) : AdminInstruction

    data class JumpOnCastFail(
        val operand: FusedOperand,
        val offset: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<Instruction> = emptyList(),
        val adjustment: StackAdjustment = StackAdjustment(depth = 0, keep = 0),
    ) : AdminInstruction

    data class PushHandler(
        val handlers: List<ControlInstruction.CatchHandler>,
        val offsets: List<Int>,
        val payloadDestinationSlots: List<List<Int>> = emptyList(),
        val endOffset: Int,
    ) : AdminInstruction

    data object PopHandler : AdminInstruction

    data object Pause : AdminInstruction

    data object PauseIf : AdminInstruction
}
