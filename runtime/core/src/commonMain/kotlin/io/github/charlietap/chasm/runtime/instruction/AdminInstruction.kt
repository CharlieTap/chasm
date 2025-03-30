package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.instruction.StackAdjustment
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.jvm.JvmInline

sealed interface AdminInstruction : LinkedInstruction {

    @JvmInline
    value class Handler(val handler: ExceptionHandler) : AdminInstruction

    data class Jump(
        val offset: Int,
    ) : AdminInstruction

    data class JumpAdjusting(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : AdminInstruction

    data class JumpIf(
        val offset: Int,
        val adjustment: StackAdjustment,
    ) : AdminInstruction

    data class JumpIfNot(
        val offset: Int,
    ) : AdminInstruction

    data class JumpTable(
        val offsets: MutableList<Int>,
        val adjustments: List<StackAdjustment>,
        val defaultOffset: Int,
        val defaultAdjustment: StackAdjustment,
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
        val adjustment: StackAdjustment,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : AdminInstruction

    data class JumpOnCastFail(
        val offset: Int,
        val adjustment: StackAdjustment,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : AdminInstruction

    data class ReturnFunction(val adjustment: StackAdjustment) : AdminInstruction
}
