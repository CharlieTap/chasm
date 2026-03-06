package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.jvm.JvmInline

sealed interface AdminInstruction : LinkedInstruction {

    data object EndBlock : AdminInstruction

    data object EndFunction : AdminInstruction

    data class CopySlots(
        val sourceSlots: List<Int>,
        val destinationSlots: List<Int>,
    ) : AdminInstruction

    data class Jump(
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
    ) : AdminInstruction

    data class JumpIfI(
        val operand: Long,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpIfS(
        val operandSlot: Int,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpTableI(
        val operand: Int,
        var continuations: List<Array<DispatchableInstruction>>,
        var defaultContinuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val takenInstructions: List<List<DispatchableInstruction>>,
        val defaultTakenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpTableS(
        val operandSlot: Int,
        var continuations: List<Array<DispatchableInstruction>>,
        var defaultContinuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val takenInstructions: List<List<DispatchableInstruction>>,
        val defaultTakenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpOnNullI(
        val operand: Long,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpOnNullS(
        val operandSlot: Int,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpOnNonNullI(
        val operand: Long,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpOnNonNullS(
        val operandSlot: Int,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpOnCastI(
        val operand: Long,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpOnCastS(
        val operandSlot: Int,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpOnCastFailI(
        val operand: Long,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class JumpOnCastFailS(
        val operandSlot: Int,
        var continuation: Array<DispatchableInstruction>,
        val discardCount: Int,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<DispatchableInstruction>,
    ) : AdminInstruction

    data class PushHandler(
        val handlers: List<CatchHandler>,
        var continuations: List<Array<DispatchableInstruction>>,
        val payloadDestinationSlots: List<List<Int>> = emptyList(),
        val discardCount: Int,
    ) : AdminInstruction

    data object PopHandler : AdminInstruction

    @JvmInline
    value class Handler(val handler: ExceptionHandler) : AdminInstruction

    data object Pause : AdminInstruction

    data object PauseIf : AdminInstruction
}
