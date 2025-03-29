package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.type.ReferenceType

sealed interface AdminInstruction : Instruction {

    data object EndBlock : AdminInstruction

    data object EndFunction : AdminInstruction

    data class Jump(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : ControlInstruction

    data class JumpIf(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : ControlInstruction

    data class JumpIfNot(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : ControlInstruction

    data class JumpTable(
        val offsets: List<Int>,
        val defaultOffset: Int,
        val adjustments: List<StackAdjustment>,
    ) : ControlInstruction

    data class JumpOnNull(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : ControlInstruction

    data class JumpOnNonNull(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : ControlInstruction

    data class JumpOnCast(
        val offset: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val adjustment: StackAdjustment,
    ) : ControlInstruction

    data class JumpOnCastFail(
        val offset: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val adjustment: StackAdjustment,
    ) : ControlInstruction
}
