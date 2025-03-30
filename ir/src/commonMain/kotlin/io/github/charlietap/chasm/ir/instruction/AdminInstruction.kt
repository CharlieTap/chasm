package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.type.ReferenceType

sealed interface AdminInstruction : Instruction {

    sealed interface JumpInstruction : AdminInstruction {

        data class Jump(
            var offset: Int,
        ) : JumpInstruction

        data class JumpAdjusting(
            var offset: Int,
            val adjustment: StackAdjustment,
        ) : JumpInstruction

        data class JumpIf(
            var offset: Int,
            val adjustment: StackAdjustment,
        ) : JumpInstruction

        data class JumpIfNot(
            var offset: Int,
        ) : JumpInstruction

        data class JumpTable(
            val offsets: MutableList<Int>,
            val adjustments: List<StackAdjustment>,
            var defaultOffset: Int,
            val defaultAdjustment: StackAdjustment,
        ) : JumpInstruction

        data class JumpOnNull(
            var offset: Int,
            val adjustment: StackAdjustment,
        ) : JumpInstruction

        data class JumpOnNonNull(
            var offset: Int,
            val adjustment: StackAdjustment,
        ) : JumpInstruction

        data class JumpOnCast(
            var offset: Int,
            val adjustment: StackAdjustment,
            val srcReferenceType: ReferenceType,
            val dstReferenceType: ReferenceType,
        ) : JumpInstruction

        data class JumpOnCastFail(
            var offset: Int,
            val adjustment: StackAdjustment,
            val srcReferenceType: ReferenceType,
            val dstReferenceType: ReferenceType,
        ) : JumpInstruction
    }

    data class ReturnFunction(val adjustment: StackAdjustment) : AdminInstruction
}
