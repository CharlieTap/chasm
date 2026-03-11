package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Module

internal fun JumpPass(
    context: PassContext,
    module: Module,
): Module =
    module.copy(
        functions = module.functions.map { function ->
            if (!function.frameSlotMode) {
                function
            } else {
                function.copy(
                    body = Expression(
                        instructions = JumpInstructionLowerer(function.body.instructions),
                    ),
                )
            }
        },
    )

private fun JumpInstructionLowerer(
    instructions: List<Instruction>,
): List<Instruction> = JumpSequenceLowerer(
    instructions = instructions,
    suffix = functionSuffix(instructions),
    handlers = ArrayDeque(),
)

private fun JumpSequenceLowerer(
    instructions: List<Instruction>,
    suffix: SequenceSuffix,
    handlers: ArrayDeque<ActiveTryHandler>,
): List<Instruction> {
    val lowerableInstructions = instructions.dropLast(suffix.dropCount)
    val lowered = mutableListOf<Instruction>()
    val labels = ArrayDeque<JumpLabel>()

    if (suffix.hasRootLabel) {
        val functionLabel = JumpLabel(
            targetIndex = null,
            handlerDepth = handlers.size,
        )
        labels.addLast(functionLabel)
        lowerInstructions(lowerableInstructions, lowered, labels, handlers)
        labels.removeLast()
        patchJumpHoles(lowered, functionLabel, lowered.size)
    } else {
        lowerInstructions(lowerableInstructions, lowered, labels, handlers)
    }

    lowered.addAll(suffix.instructions)
    return lowered
}

private fun functionSuffix(
    instructions: List<Instruction>,
): SequenceSuffix = when {
    instructions.lastOrNull() is AdminInstruction.EndFunction ->
        SequenceSuffix(
            instructions = instructions.takeLast(1),
            dropCount = 1,
            hasRootLabel = true,
        )
    else -> SequenceSuffix()
}

private fun lowerInstructions(
    instructions: List<Instruction>,
    output: MutableList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
) {
    instructions.forEach { instruction ->
        lowerInstruction(instruction, output, labels, handlers)
    }
}

private fun lowerInstruction(
    instruction: Instruction,
    output: MutableList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
) {
    when (instruction) {
        is AdminInstruction.EndBlock -> Unit
        is ControlInstruction.TryTable -> lowerTryTable(instruction, output, labels, handlers)
        is ControlInstruction.Block -> lowerBlock(instruction.instructions, output, labels, handlers)
        is ControlInstruction.Loop -> lowerLoop(instruction.instructions, output, labels, handlers)
        is ControlSuperInstruction.If -> lowerIf(instruction, output, labels, handlers)
        is ControlInstruction.Br -> lowerJump(
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
        )
        is ControlSuperInstruction.BrIf -> lowerJumpIf(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
        )
        is ControlSuperInstruction.BrTable -> lowerJumpTable(
            instruction = instruction,
            labels = labels,
            currentHandlerDepth = handlers.size,
            output = output,
        )
        is ControlSuperInstruction.BrOnNull -> lowerJumpOnNull(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
        )
        is ControlSuperInstruction.BrOnNonNull -> lowerJumpOnNonNull(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
        )
        is ControlSuperInstruction.BrOnCast -> lowerJumpOnCast(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
        )
        is ControlSuperInstruction.BrOnCastFail -> lowerJumpOnCastFail(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
        )
        else -> output.add(instruction)
    }
}

private fun lowerBlock(
    instructions: List<Instruction>,
    output: MutableList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
) {
    val label = JumpLabel(
        targetIndex = null,
        handlerDepth = handlers.size,
    )
    labels.addLast(label)
    lowerInstructions(instructions, output, labels, handlers)
    labels.removeLast()
    patchJumpHoles(output, label, output.size)
}

private fun lowerLoop(
    instructions: List<Instruction>,
    output: MutableList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
) {
    val label = JumpLabel(
        targetIndex = output.size,
        handlerDepth = handlers.size,
    )
    labels.addLast(label)
    lowerInstructions(instructions, output, labels, handlers)
    labels.removeLast()
}

private fun lowerIf(
    instruction: ControlSuperInstruction.If,
    output: MutableList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
) {
    val label = JumpLabel(
        targetIndex = null,
        handlerDepth = handlers.size,
    )
    labels.addLast(label)

    val jumpToThenIndex = output.size
    output.add(
        AdminInstruction.JumpIf(
            operand = instruction.operand,
            offset = UNPATCHED_OFFSET,
        ),
    )

    lowerInstructions(instruction.elseInstructions.orEmpty(), output, labels, handlers)

    val jumpPastThenIndex = output.size
    output.add(AdminInstruction.Jump(offset = UNPATCHED_OFFSET))

    val thenStart = output.size
    output[jumpToThenIndex] = (output[jumpToThenIndex] as AdminInstruction.JumpIf).copy(offset = thenStart)

    lowerInstructions(instruction.thenInstructions, output, labels, handlers)

    labels.removeLast()
    val endIndex = output.size
    output[jumpPastThenIndex] = (output[jumpPastThenIndex] as AdminInstruction.Jump).copy(offset = endIndex)
    patchJumpHoles(output, label, endIndex)
}

private fun lowerTryTable(
    instruction: ControlInstruction.TryTable,
    output: MutableList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
) {
    val pushHandlerInstructionIndex = output.size
    output.add(
        AdminInstruction.PushHandler(
            handlers = instruction.handlers,
            offsets = instruction.handlers.map { handler ->
                jumpTarget(labels, handler.labelIndex).targetIndex ?: UNPATCHED_OFFSET
            },
            payloadDestinationSlots = instruction.payloadDestinationSlots,
            endOffset = UNPATCHED_OFFSET,
        ),
    )

    instruction.handlers.forEachIndexed { handlerIndex, handler ->
        val target = jumpTarget(labels, handler.labelIndex)
        if (target.targetIndex == null) {
            target.holes.add(
                HandlerTargetHole(
                    instructionIndex = pushHandlerInstructionIndex,
                    handlerIndex = handlerIndex,
                ),
            )
        }
    }

    val label = JumpLabel(
        targetIndex = null,
        handlerDepth = handlers.size + 1,
    )
    labels.addLast(label)
    handlers.addLast(ActiveTryHandler)
    lowerInstructions(instruction.instructions, output, labels, handlers)
    handlers.removeLast()
    labels.removeLast()

    val popHandlerIndex = output.size
    output[pushHandlerInstructionIndex] = (output[pushHandlerInstructionIndex] as AdminInstruction.PushHandler).copy(
        endOffset = popHandlerIndex,
    )
    patchJumpHoles(output, label, popHandlerIndex)
    output.add(AdminInstruction.PopHandler)
}

private fun lowerJump(
    target: JumpLabel,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
) {
    output.addAll(handlerExitInstructions(currentHandlerDepth, target))
    val instructionIndex = output.size
    output.add(AdminInstruction.Jump(offset = target.targetIndex ?: UNPATCHED_OFFSET))
    if (target.targetIndex == null) {
        target.holes.add(OffsetJumpHole(instructionIndex))
    }
}

private fun lowerJumpIf(
    instruction: ControlSuperInstruction.BrIf,
    target: JumpLabel,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpIf(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
            takenInstructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
        ),
    )
    if (target.targetIndex == null) {
        target.holes.add(OffsetJumpHole(instructionIndex))
    }
}

private fun lowerJumpTable(
    instruction: ControlSuperInstruction.BrTable,
    labels: ArrayDeque<JumpLabel>,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
) {
    val targets = instruction.labelIndices.map { labelIndex ->
        jumpTarget(labels, labelIndex)
    }
    val defaultTarget = jumpTarget(labels, instruction.defaultLabelIndex)
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpTable(
            operand = instruction.operand,
            offsets = targets.map { target -> target.targetIndex ?: UNPATCHED_OFFSET },
            defaultOffset = defaultTarget.targetIndex ?: UNPATCHED_OFFSET,
            takenInstructions = instruction.takenInstructions.mapIndexed { index, takenInstructions ->
                takenInstructions + handlerExitInstructions(currentHandlerDepth, targets[index])
            },
            defaultTakenInstructions = instruction.defaultTakenInstructions +
                handlerExitInstructions(currentHandlerDepth, defaultTarget),
        ),
    )

    targets.forEachIndexed { index, target ->
        if (target.targetIndex == null) {
            target.holes.add(JumpTableHole(instructionIndex, index))
        }
    }

    if (defaultTarget.targetIndex == null) {
        defaultTarget.holes.add(JumpTableHole(instructionIndex, null))
    }
}

private fun lowerJumpOnNull(
    instruction: ControlSuperInstruction.BrOnNull,
    target: JumpLabel,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpOnNull(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
            takenInstructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
        ),
    )
    if (target.targetIndex == null) {
        target.holes.add(OffsetJumpHole(instructionIndex))
    }
}

private fun lowerJumpOnNonNull(
    instruction: ControlSuperInstruction.BrOnNonNull,
    target: JumpLabel,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpOnNonNull(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
            takenInstructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
        ),
    )
    if (target.targetIndex == null) {
        target.holes.add(OffsetJumpHole(instructionIndex))
    }
}

private fun lowerJumpOnCast(
    instruction: ControlSuperInstruction.BrOnCast,
    target: JumpLabel,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpOnCast(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
            takenInstructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
        ),
    )
    if (target.targetIndex == null) {
        target.holes.add(OffsetJumpHole(instructionIndex))
    }
}

private fun lowerJumpOnCastFail(
    instruction: ControlSuperInstruction.BrOnCastFail,
    target: JumpLabel,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpOnCastFail(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
            takenInstructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
        ),
    )
    if (target.targetIndex == null) {
        target.holes.add(OffsetJumpHole(instructionIndex))
    }
}

private fun patchJumpHoles(
    output: MutableList<Instruction>,
    label: JumpLabel,
    targetIndex: Int,
) {
    label.targetIndex = targetIndex
    label.holes.forEach { hole ->
        hole.patch(output, targetIndex)
    }
}

private fun handlerExitInstructions(
    currentHandlerDepth: Int,
    target: JumpLabel,
): List<Instruction> {
    val handlerPopCount = currentHandlerDepth - target.handlerDepth
    require(handlerPopCount >= 0) {
        "cannot jump into a deeper handler scope"
    }

    return List(handlerPopCount) { AdminInstruction.PopHandler }
}

private fun jumpTarget(
    labels: ArrayDeque<JumpLabel>,
    labelIndex: Index.LabelIndex,
): JumpLabel {
    val targetIndex = labels.size - 1 - labelIndex.idx
    require(targetIndex in 0 until labels.size) {
        "invalid jump label index ${labelIndex.idx}"
    }
    return labels.elementAt(targetIndex)
}

private fun patchOffsetInstruction(
    instruction: Instruction,
    targetIndex: Int,
): Instruction = when (instruction) {
    is AdminInstruction.Jump -> instruction.copy(offset = targetIndex)
    is AdminInstruction.JumpIf -> instruction.copy(offset = targetIndex)
    is AdminInstruction.JumpOnNull -> instruction.copy(offset = targetIndex)
    is AdminInstruction.JumpOnNonNull -> instruction.copy(offset = targetIndex)
    is AdminInstruction.JumpOnCast -> instruction.copy(offset = targetIndex)
    is AdminInstruction.JumpOnCastFail -> instruction.copy(offset = targetIndex)
    else -> error("unsupported jump instruction hole patch: $instruction")
}

private fun patchJumpTableInstruction(
    instruction: Instruction,
    branchIndex: Int?,
    targetIndex: Int,
): Instruction {
    val jumpTable = instruction as? AdminInstruction.JumpTable
        ?: error("unsupported jump-table instruction hole patch: $instruction")

    return if (branchIndex != null) {
        val patchedOffsets = jumpTable.offsets.toMutableList()
        patchedOffsets[branchIndex] = targetIndex
        jumpTable.copy(offsets = patchedOffsets)
    } else {
        jumpTable.copy(defaultOffset = targetIndex)
    }
}

private fun patchHandlerOffsetInstruction(
    instruction: Instruction,
    handlerIndex: Int,
    targetIndex: Int,
): Instruction {
    val pushHandler = instruction as? AdminInstruction.PushHandler
        ?: error("unsupported handler target hole patch: $instruction")

    val patchedOffsets = pushHandler.offsets.toMutableList()
    patchedOffsets[handlerIndex] = targetIndex
    return pushHandler.copy(offsets = patchedOffsets)
}

private data class JumpLabel(
    var targetIndex: Int?,
    val handlerDepth: Int,
    val holes: MutableList<JumpHole> = mutableListOf(),
)

private data object ActiveTryHandler

private data class SequenceSuffix(
    val instructions: List<Instruction> = emptyList(),
    val dropCount: Int = 0,
    val hasRootLabel: Boolean = false,
)

private sealed interface JumpHole {
    val instructionIndex: Int

    fun patch(output: MutableList<Instruction>, targetIndex: Int)
}

private data class OffsetJumpHole(
    override val instructionIndex: Int,
) : JumpHole {
    override fun patch(
        output: MutableList<Instruction>,
        targetIndex: Int,
    ) {
        output[instructionIndex] = patchOffsetInstruction(output[instructionIndex], targetIndex)
    }
}

private data class JumpTableHole(
    override val instructionIndex: Int,
    val branchIndex: Int?,
) : JumpHole {
    override fun patch(
        output: MutableList<Instruction>,
        targetIndex: Int,
    ) {
        output[instructionIndex] = patchJumpTableInstruction(output[instructionIndex], branchIndex, targetIndex)
    }
}

private data class HandlerTargetHole(
    override val instructionIndex: Int,
    val handlerIndex: Int,
) : JumpHole {
    override fun patch(
        output: MutableList<Instruction>,
        targetIndex: Int,
    ) {
        output[instructionIndex] = patchHandlerOffsetInstruction(output[instructionIndex], handlerIndex, targetIndex)
    }
}

private const val UNPATCHED_OFFSET = -1
