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
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction as RuntimeAdminInstruction

internal fun InstructionSequencePredecoder(
    context: PredecodingContext,
    instructions: List<Instruction>,
): Result<Array<DispatchableInstruction>, ModuleTrapError> = binding {
    val dispatchables = InstructionSequencePredecoderList(context, instructions).bind()

    Array(dispatchables.size) { index ->
        val reversedIndex = dispatchables.size - 1 - index
        dispatchables[reversedIndex]
    }
}

internal fun InstructionSequencePredecoderList(
    context: PredecodingContext,
    instructions: List<Instruction>,
): Result<List<DispatchableInstruction>, ModuleTrapError> = binding {
    val dispatchables = MutableList<DispatchableInstruction?>(instructions.size) { null }
    val patches = mutableListOf<() -> Unit>()

    instructions.forEachIndexed { index, instruction ->
        dispatchables[index] = when (instruction) {
            is AdminInstruction.Jump -> predecodeJump(instruction, instructions.size, index, patches, dispatchables)
            is AdminInstruction.JumpIf -> predecodeJumpIf(context, instruction, instructions.size, index, patches, dispatchables).bind()
            is AdminInstruction.JumpTable -> predecodeJumpTable(context, instruction, instructions.size, index, patches, dispatchables).bind()
            is AdminInstruction.JumpOnNull -> predecodeJumpOnNull(context, instruction, instructions.size, index, patches, dispatchables).bind()
            is AdminInstruction.JumpOnNonNull -> predecodeJumpOnNonNull(context, instruction, instructions.size, index, patches, dispatchables).bind()
            is AdminInstruction.JumpOnCast -> predecodeJumpOnCast(context, instruction, instructions.size, index, patches, dispatchables).bind()
            is AdminInstruction.JumpOnCastFail -> predecodeJumpOnCastFail(context, instruction, instructions.size, index, patches, dispatchables).bind()
            is AdminInstruction.PushHandler -> predecodePushHandler(instruction, index, patches, dispatchables)
            is AdminInstruction.PopHandler -> PopHandlerDispatcher(RuntimeAdminInstruction.PopHandler)
            else -> InstructionPredecoder(context, instruction).bind()
        }
    }

    patches.forEach { patch -> patch() }

    dispatchables.map { dispatchable ->
        dispatchable ?: error("instruction sequence predecode must patch all dispatchables")
    }
}

private fun predecodeJump(
    instruction: AdminInstruction.Jump,
    instructionCount: Int,
    index: Int,
    patches: MutableList<() -> Unit>,
    dispatchables: List<DispatchableInstruction?>,
): DispatchableInstruction {
    val runtimeInstruction = RuntimeAdminInstruction.Jump(
        continuation = emptyArray(),
        discardCount = remainingInstructionCount(instructionCount, index),
    )
    patches += {
        runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
    }
    return JumpDispatcher(runtimeInstruction)
}

private fun predecodePushHandler(
    instruction: AdminInstruction.PushHandler,
    index: Int,
    patches: MutableList<() -> Unit>,
    dispatchables: List<DispatchableInstruction?>,
): DispatchableInstruction {
    val runtimeInstruction = RuntimeAdminInstruction.PushHandler(
        handlers = instruction.handlers,
        continuations = List(instruction.offsets.size) { emptyArray() },
        payloadDestinationSlots = instruction.payloadDestinationSlots,
        discardCount = instruction.endOffset - index,
    )
    patches += {
        runtimeInstruction.continuations = instruction.offsets.map { offset ->
            continuationForOffset(dispatchables, offset)
        }
    }
    return PushHandlerDispatcher(runtimeInstruction)
}

private fun predecodeJumpIf(
    context: PredecodingContext,
    instruction: AdminInstruction.JumpIf,
    instructionCount: Int,
    index: Int,
    patches: MutableList<() -> Unit>,
    dispatchables: List<DispatchableInstruction?>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val discardCount = remainingInstructionCount(instructionCount, index)
    val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)

    when {
        operandImmediate != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpIfI(
                operand = operandImmediate,
                continuation = emptyArray(),
                discardCount = discardCount,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        operandSlot != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpIfS(
                operandSlot = operandSlot,
                continuation = emptyArray(),
                discardCount = discardCount,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpTable(
    context: PredecodingContext,
    instruction: AdminInstruction.JumpTable,
    instructionCount: Int,
    index: Int,
    patches: MutableList<() -> Unit>,
    dispatchables: List<DispatchableInstruction?>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val discardCount = remainingInstructionCount(instructionCount, index)
    val takenInstructions = instruction.takenInstructions.map { instructions ->
        InstructionSequencePredecoderList(context, instructions).bind()
    }
    val defaultTakenInstructions = InstructionSequencePredecoderList(context, instruction.defaultTakenInstructions).bind()
    val operandImmediate = jumpIndexImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)

    when {
        operandImmediate != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpTableI(
                operand = operandImmediate,
                continuations = emptyList(),
                defaultContinuation = emptyArray(),
                discardCount = discardCount,
                takenInstructions = takenInstructions,
                defaultTakenInstructions = defaultTakenInstructions,
            )
            patches += {
                runtimeInstruction.continuations = instruction.offsets.map { offset ->
                    continuationForOffset(dispatchables, offset)
                }
                runtimeInstruction.defaultContinuation = continuationForOffset(dispatchables, instruction.defaultOffset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        operandSlot != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpTableS(
                operandSlot = operandSlot,
                continuations = emptyList(),
                defaultContinuation = emptyArray(),
                discardCount = discardCount,
                takenInstructions = takenInstructions,
                defaultTakenInstructions = defaultTakenInstructions,
            )
            patches += {
                runtimeInstruction.continuations = instruction.offsets.map { offset ->
                    continuationForOffset(dispatchables, offset)
                }
                runtimeInstruction.defaultContinuation = continuationForOffset(dispatchables, instruction.defaultOffset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpOnNull(
    context: PredecodingContext,
    instruction: AdminInstruction.JumpOnNull,
    instructionCount: Int,
    index: Int,
    patches: MutableList<() -> Unit>,
    dispatchables: List<DispatchableInstruction?>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val discardCount = remainingInstructionCount(instructionCount, index)
    val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)

    when {
        operandImmediate != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpOnNullI(
                operand = operandImmediate,
                continuation = emptyArray(),
                discardCount = discardCount,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        operandSlot != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpOnNullS(
                operandSlot = operandSlot,
                continuation = emptyArray(),
                discardCount = discardCount,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpOnNonNull(
    context: PredecodingContext,
    instruction: AdminInstruction.JumpOnNonNull,
    instructionCount: Int,
    index: Int,
    patches: MutableList<() -> Unit>,
    dispatchables: List<DispatchableInstruction?>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val discardCount = remainingInstructionCount(instructionCount, index)
    val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)

    when {
        operandImmediate != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpOnNonNullI(
                operand = operandImmediate,
                continuation = emptyArray(),
                discardCount = discardCount,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        operandSlot != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpOnNonNullS(
                operandSlot = operandSlot,
                continuation = emptyArray(),
                discardCount = discardCount,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpOnCast(
    context: PredecodingContext,
    instruction: AdminInstruction.JumpOnCast,
    instructionCount: Int,
    index: Int,
    patches: MutableList<() -> Unit>,
    dispatchables: List<DispatchableInstruction?>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val discardCount = remainingInstructionCount(instructionCount, index)
    val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)

    when {
        operandImmediate != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpOnCastI(
                operand = operandImmediate,
                continuation = emptyArray(),
                discardCount = discardCount,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        operandSlot != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpOnCastS(
                operandSlot = operandSlot,
                continuation = emptyArray(),
                discardCount = discardCount,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun predecodeJumpOnCastFail(
    context: PredecodingContext,
    instruction: AdminInstruction.JumpOnCastFail,
    instructionCount: Int,
    index: Int,
    patches: MutableList<() -> Unit>,
    dispatchables: List<DispatchableInstruction?>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val discardCount = remainingInstructionCount(instructionCount, index)
    val takenInstructions = InstructionSequencePredecoderList(context, instruction.takenInstructions).bind()
    val operandImmediate = jumpImmediate(instruction.operand)
    val operandSlot = jumpOperandSlot(instruction.operand)

    when {
        operandImmediate != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpOnCastFailI(
                operand = operandImmediate,
                continuation = emptyArray(),
                discardCount = discardCount,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        operandSlot != null -> {
            val runtimeInstruction = RuntimeAdminInstruction.JumpOnCastFailS(
                operandSlot = operandSlot,
                continuation = emptyArray(),
                discardCount = discardCount,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
                takenInstructions = takenInstructions,
            )
            patches += {
                runtimeInstruction.continuation = continuationForOffset(dispatchables, instruction.offset)
            }
            JumpDispatcher(runtimeInstruction)
        }

        else -> unsupportedUnloweredJumpInstruction()
    }
}

private fun remainingInstructionCount(
    instructionCount: Int,
    index: Int,
): Int = instructionCount - index - 1

private fun continuationForOffset(
    dispatchables: List<DispatchableInstruction?>,
    offset: Int,
): Array<DispatchableInstruction> {
    require(offset in 0..dispatchables.size) {
        "jump target offset $offset is outside instruction sequence of size ${dispatchables.size}"
    }
    if (offset == dispatchables.size) return emptyArray()

    val continuation = dispatchables.subList(offset, dispatchables.size).map { dispatchable ->
        dispatchable ?: error("jump target continuation must be patched after all dispatchables are populated")
    }

    return Array(continuation.size) { index ->
        val reversedIndex = continuation.size - 1 - index
        continuation[reversedIndex]
    }
}

private fun jumpImmediate(
    operand: FusedOperand,
): Long? = when (operand) {
    is FusedOperand.I32Const -> operand.const.toLong()
    is FusedOperand.I64Const -> operand.const
    is FusedOperand.F32Const -> operand.const.toRawBits().toLong()
    is FusedOperand.F64Const -> operand.const.toRawBits()
    else -> null
}

private fun jumpIndexImmediate(
    operand: FusedOperand,
): Int? = jumpImmediate(operand)?.toInt()

private fun jumpOperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun unsupportedUnloweredJumpInstruction(): DispatchableInstruction =
    error("jump instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")
