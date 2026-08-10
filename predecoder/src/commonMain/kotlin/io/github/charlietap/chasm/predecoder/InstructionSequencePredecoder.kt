package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PopHandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PushHandlerDispatcher
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction as RuntimeAdminInstruction

internal fun InstructionSequencePredecoder(
    context: PredecodingContext,
    instructions: List<Instruction>,
    baseIp: Int = context.store.program.size,
): Result<Array<DispatchableInstruction>, ModuleTrapError> = binding {
    Array(instructions.size) { index ->
        predecodeInstruction(context, instructions[index], baseIp).bind()
    }
}

private fun predecodeInstruction(
    context: PredecodingContext,
    instruction: Instruction,
    baseIp: Int,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is AdminInstruction.Jump -> JumpDispatcher(
            RuntimeAdminInstruction.Jump(targetIp = baseIp + instruction.offset),
        )
        is AdminInstruction.JumpIf -> predecodeJumpIf(instruction, baseIp)
        is AdminInstruction.JumpIfCopy -> predecodeJumpIfCopy(instruction, baseIp)
        is AdminInstruction.JumpIfCondition -> JumpDispatcher(
            RuntimeAdminInstruction.JumpIfCondition(
                condition = instruction.condition,
                targetIp = baseIp + instruction.offset,
            ),
        )
        is AdminInstruction.JumpTable -> predecodeJumpTable(instruction, baseIp)
        is AdminInstruction.JumpOnNull -> predecodeJumpOnNull(instruction, baseIp)
        is AdminInstruction.JumpOnNonNull -> predecodeJumpOnNonNull(instruction, baseIp)
        is AdminInstruction.JumpOnCast -> predecodeJumpOnCast(context, instruction, baseIp)
        is AdminInstruction.JumpOnCastFail -> predecodeJumpOnCastFail(context, instruction, baseIp)
        is AdminInstruction.PushHandler -> PushHandlerDispatcher(
            RuntimeAdminInstruction.PushHandler(
                handlers = instruction.handlers,
                continuationIps = IntArray(instruction.offsets.size) { index ->
                    baseIp + instruction.offsets[index]
                },
                payloadDestinationSlots = instruction.payloadDestinationSlots,
            ),
        )
        is AdminInstruction.PopHandler -> PopHandlerDispatcher(RuntimeAdminInstruction.PopHandler)
        else -> InstructionPredecoder(context, instruction).bind()
    }
}

private fun predecodeJumpIf(
    instruction: AdminInstruction.JumpIf,
    baseIp: Int,
): DispatchableInstruction {
    val targetIp = baseIp + instruction.offset
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)
    return when {
        operandImmediate != null -> JumpDispatcher(RuntimeAdminInstruction.JumpIfI(operandImmediate, targetIp))
        operandSlot != null -> JumpDispatcher(RuntimeAdminInstruction.JumpIfS(operandSlot, targetIp))
        instruction.operand is FusedOperand.ValueStack -> JumpDispatcher(RuntimeAdminInstruction.JumpIfV(targetIp))
        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpIfCopy(
    instruction: AdminInstruction.JumpIfCopy,
    baseIp: Int,
): DispatchableInstruction {
    val targetIp = baseIp + instruction.offset
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)
    return when {
        operandImmediate != null -> JumpDispatcher(
            RuntimeAdminInstruction.JumpIfCopyI(
                operand = operandImmediate,
                sourceSlot = instruction.sourceSlot,
                destinationSlot = instruction.destinationSlot,
                targetIp = targetIp,
            ),
        )
        operandSlot != null -> JumpDispatcher(
            RuntimeAdminInstruction.JumpIfCopyS(
                operandSlot = operandSlot,
                sourceSlot = instruction.sourceSlot,
                destinationSlot = instruction.destinationSlot,
                targetIp = targetIp,
            ),
        )
        instruction.operand is FusedOperand.ValueStack -> JumpDispatcher(
            RuntimeAdminInstruction.JumpIfCopyV(
                sourceSlot = instruction.sourceSlot,
                destinationSlot = instruction.destinationSlot,
                targetIp = targetIp,
            ),
        )
        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpTable(
    instruction: AdminInstruction.JumpTable,
    baseIp: Int,
): DispatchableInstruction {
    val targetIps = IntArray(instruction.offsets.size) { index ->
        baseIp + instruction.offsets[index]
    }
    val defaultTargetIp = baseIp + instruction.defaultOffset
    val operandImmediate = jumpIndexImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)
    return when {
        operandImmediate != null -> JumpDispatcher(
            RuntimeAdminInstruction.JumpTableI(operandImmediate, targetIps, defaultTargetIp),
        )
        operandSlot != null -> JumpDispatcher(
            RuntimeAdminInstruction.JumpTableS(operandSlot, targetIps, defaultTargetIp),
        )
        instruction.operand is FusedOperand.ValueStack -> JumpDispatcher(
            RuntimeAdminInstruction.JumpTableV(targetIps, defaultTargetIp),
        )
        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpOnNull(
    instruction: AdminInstruction.JumpOnNull,
    baseIp: Int,
): DispatchableInstruction {
    val targetIp = baseIp + instruction.offset
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)
    return when {
        operandImmediate != null -> JumpDispatcher(RuntimeAdminInstruction.JumpOnNullI(operandImmediate, targetIp))
        operandSlot != null -> JumpDispatcher(RuntimeAdminInstruction.JumpOnNullS(operandSlot, targetIp))
        instruction.operand is FusedOperand.ValueStack -> JumpDispatcher(RuntimeAdminInstruction.JumpOnNullV(targetIp))
        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpOnNonNull(
    instruction: AdminInstruction.JumpOnNonNull,
    baseIp: Int,
): DispatchableInstruction {
    val targetIp = baseIp + instruction.offset
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)
    return when {
        operandImmediate != null -> JumpDispatcher(RuntimeAdminInstruction.JumpOnNonNullI(operandImmediate, targetIp))
        operandSlot != null -> JumpDispatcher(RuntimeAdminInstruction.JumpOnNonNullS(operandSlot, targetIp))
        instruction.operand is FusedOperand.ValueStack -> JumpDispatcher(RuntimeAdminInstruction.JumpOnNonNullV(targetIp))
        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpOnCast(
    context: PredecodingContext,
    instruction: AdminInstruction.JumpOnCast,
    baseIp: Int,
): DispatchableInstruction {
    hydrateReferenceType(context, instruction.dstReferenceType.heapType)
    val targetIp = baseIp + instruction.offset
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)
    return when {
        operandImmediate != null -> JumpDispatcher(
            RuntimeAdminInstruction.JumpOnCastI(
                operandImmediate,
                targetIp,
                instruction.srcReferenceType,
                instruction.dstReferenceType,
            ),
        )
        operandSlot != null -> JumpDispatcher(
            RuntimeAdminInstruction.JumpOnCastS(
                operandSlot,
                targetIp,
                instruction.srcReferenceType,
                instruction.dstReferenceType,
            ),
        )
        instruction.operand is FusedOperand.ValueStack -> JumpDispatcher(
            RuntimeAdminInstruction.JumpOnCastV(
                targetIp,
                instruction.srcReferenceType,
                instruction.dstReferenceType,
            ),
        )
        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpOnCastFail(
    context: PredecodingContext,
    instruction: AdminInstruction.JumpOnCastFail,
    baseIp: Int,
): DispatchableInstruction {
    hydrateReferenceType(context, instruction.dstReferenceType.heapType)
    val targetIp = baseIp + instruction.offset
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)
    return when {
        operandImmediate != null -> JumpDispatcher(
            RuntimeAdminInstruction.JumpOnCastFailI(
                operandImmediate,
                targetIp,
                instruction.srcReferenceType,
                instruction.dstReferenceType,
            ),
        )
        operandSlot != null -> JumpDispatcher(
            RuntimeAdminInstruction.JumpOnCastFailS(
                operandSlot,
                targetIp,
                instruction.srcReferenceType,
                instruction.dstReferenceType,
            ),
        )
        instruction.operand is FusedOperand.ValueStack -> JumpDispatcher(
            RuntimeAdminInstruction.JumpOnCastFailV(
                targetIp,
                instruction.srcReferenceType,
                instruction.dstReferenceType,
            ),
        )
        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun jumpImmediate(operand: FusedOperand): Long? = when (operand) {
    is FusedOperand.I32Const -> operand.const.toLong()
    is FusedOperand.I64Const -> operand.const
    is FusedOperand.F32Const -> operand.const.toRawBits().toLong()
    is FusedOperand.F64Const -> operand.const.toRawBits()
    else -> null
}

private fun jumpIndexImmediate(operand: FusedOperand): Int? = jumpImmediate(operand)?.toInt()

private fun jumpOperandSlot(operand: FusedOperand): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun hydrateReferenceType(
    context: PredecodingContext,
    heapType: HeapType,
) {
    if (heapType is ConcreteHeapType.TypeIndex) {
        context.instance.runtimeTypes[heapType.index].hydrate()
    }
}

private fun unsupportedUnloweredJumpInstruction(): DispatchableInstruction =
    error("jump instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")
