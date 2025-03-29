package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.type.ReferenceType

sealed interface AdminInstruction : Instruction {

    data object EndBlock : AdminInstruction

    data object EndFunction : AdminInstruction

    data class Jump(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : AdminInstruction

    data class JumpIf(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : AdminInstruction

    data class JumpTable(
        val offsets: List<Int>,
        val defaultOffset: Int,
        val adjustments: List<StackAdjustment>,
    ) : AdminInstruction

    data class JumpOnNull(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : AdminInstruction

    data class JumpOnNonNull(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : AdminInstruction

    data class JumpOnCast(
        val offset: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val adjustment: StackAdjustment,
    ) : AdminInstruction

    data class JumpOnCastFail(
        val offset: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val adjustment: StackAdjustment,
    ) : AdminInstruction
}
