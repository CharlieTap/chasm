package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Module

internal fun JumpPass(
    context: PassContext,
    module: Module,
): Module =
    module.copy(
        functions = module.functions.map { function ->
            val loweredInstructions = JumpInstructionLowerer(function.body.instructions)
            function.copy(
                body = Expression(
                    instructions = loweredInstructions,
                ),
            )
        },
    )

private fun JumpInstructionLowerer(
    instructions: List<Instruction>,
): List<Instruction> {
    val hasRootLabel = instructions.lastOrNull() is AdminInstruction.EndFunction
    val endExclusive = instructions.size - if (hasRootLabel) 1 else 0
    val controlFlow = JumpControlFlowIndex(instructions, endExclusive)
    val output = ArrayList<Instruction>(instructions.size)
    val labels = ArrayDeque<JumpLabel>()
    val handlers = ArrayDeque<ActiveTryHandler>()
    val work = ArrayDeque<JumpWork>()
    val takenPaths = mutableListOf<TakenPath>()

    if (hasRootLabel) {
        val functionLabel = JumpLabel(
            targetIndex = null,
            handlerDepth = handlers.size,
        )
        labels.addLast(functionLabel)
        work.addLast(JumpWork.Range(0, endExclusive))
        JumpWorkLowerer(
            instructions = instructions,
            controlFlow = controlFlow,
            output = output,
            labels = labels,
            handlers = handlers,
            work = work,
            takenPaths = takenPaths,
        )
        require(labels.removeLastOrNull() === functionLabel) {
            "jump lowering left an invalid function label stack: $labels"
        }
        patchJumpHoles(output, functionLabel, output.size)
    } else {
        work.addLast(JumpWork.Range(0, endExclusive))
        JumpWorkLowerer(
            instructions = instructions,
            controlFlow = controlFlow,
            output = output,
            labels = labels,
            handlers = handlers,
            work = work,
            takenPaths = takenPaths,
        )
    }

    require(labels.isEmpty() && handlers.isEmpty() && work.isEmpty()) {
        "jump lowering left compiler control state: labels=$labels handlers=$handlers work=$work"
    }
    require(output.none(::isStructuredControlInstruction)) {
        "jump lowering left a structured control instruction in its output"
    }

    if (hasRootLabel) {
        output.add(AdminInstruction.EndFunction)
    }
    appendTakenPaths(output, takenPaths, hasRootLabel)
    return output
}

private fun JumpWorkLowerer(
    instructions: List<Instruction>,
    controlFlow: JumpControlFlow,
    output: ArrayList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
    work: ArrayDeque<JumpWork>,
    takenPaths: MutableList<TakenPath>,
) {
    while (work.isNotEmpty()) {
        when (val item = work.removeLast()) {
            is JumpWork.Range -> JumpRangeLowerer(
                range = item,
                instructions = instructions,
                controlFlow = controlFlow,
                output = output,
                labels = labels,
                handlers = handlers,
                work = work,
                takenPaths = takenPaths,
            )
            is JumpWork.CloseBlock -> {
                require(labels.removeLastOrNull() === item.label) {
                    "jump block label stack does not match its source structure"
                }
                patchJumpHoles(output, item.label, output.size)
            }
            is JumpWork.CloseLoop -> {
                require(labels.removeLastOrNull() === item.label) {
                    "jump loop label stack does not match its source structure"
                }
            }
            is JumpWork.BeginThen -> {
                item.state.jumpPastThenIndex = output.size
                output.add(AdminInstruction.Jump(offset = UNPATCHED_OFFSET))
                val thenStart = output.size
                output[item.state.jumpToThenIndex] = patchOffsetInstruction(
                    instruction = output[item.state.jumpToThenIndex],
                    targetIndex = thenStart,
                )
            }
            is JumpWork.CloseIf -> {
                require(labels.removeLastOrNull() === item.state.label) {
                    "jump if label stack does not match its source structure"
                }
                val endIndex = output.size
                val jumpPastThenIndex = item.state.jumpPastThenIndex
                require(jumpPastThenIndex >= 0) {
                    "jump if lowering did not emit its then-arm jump"
                }
                output[jumpPastThenIndex] =
                    (output[jumpPastThenIndex] as AdminInstruction.Jump).copy(offset = endIndex)
                patchJumpHoles(output, item.state.label, endIndex)
            }
            is JumpWork.CloseTryTable -> {
                require(handlers.removeLastOrNull() === ActiveTryHandler) {
                    "jump try-table handler stack does not match its source structure"
                }
                require(labels.removeLastOrNull() === item.label) {
                    "jump try-table label stack does not match its source structure"
                }

                val popHandlerIndex = output.size
                output[item.pushHandlerIndex] =
                    (output[item.pushHandlerIndex] as AdminInstruction.PushHandler).copy(
                        endOffset = popHandlerIndex,
                    )
                patchJumpHoles(output, item.label, popHandlerIndex)
                output.add(AdminInstruction.PopHandler)
            }
        }
    }
}

private fun JumpRangeLowerer(
    range: JumpWork.Range,
    instructions: List<Instruction>,
    controlFlow: JumpControlFlow,
    output: ArrayList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
    work: ArrayDeque<JumpWork>,
    takenPaths: MutableList<TakenPath>,
) {
    var index = range.start
    while (index < range.endExclusive) {
        val instruction = instructions[index]
        if (isStructuredControlOpener(instruction)) {
            val endIndex = controlFlow.endIndices[index]
            require(endIndex in (index + 1) until range.endExclusive) {
                "jump control range does not contain its end: index=$index end=$endIndex range=$range"
            }

            if (endIndex + 1 < range.endExclusive) {
                work.addLast(JumpWork.Range(endIndex + 1, range.endExclusive))
            }
            JumpControlEnterLowerer(
                sourceIndex = index,
                instruction = instruction,
                endIndex = endIndex,
                controlFlow = controlFlow,
                output = output,
                labels = labels,
                handlers = handlers,
                work = work,
            )
            return
        }

        require(instruction !is ControlInstruction.Else && instruction !is ControlInstruction.End) {
            "jump lowering encountered an unindexed control marker at $index: $instruction"
        }
        JumpInstructionLowerer(instruction, output, labels, handlers, takenPaths)
        index++
    }
}

private fun JumpControlEnterLowerer(
    sourceIndex: Int,
    instruction: Instruction,
    endIndex: Int,
    controlFlow: JumpControlFlow,
    output: ArrayList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
    work: ArrayDeque<JumpWork>,
) {
    when (instruction) {
        is ControlInstruction.Block -> {
            val label = JumpLabel(targetIndex = null, handlerDepth = handlers.size)
            labels.addLast(label)
            work.addLast(JumpWork.CloseBlock(label))
            work.addLast(JumpWork.Range(sourceIndex + 1, endIndex))
        }
        is ControlInstruction.Loop -> {
            val label = JumpLabel(targetIndex = output.size, handlerDepth = handlers.size)
            labels.addLast(label)
            work.addLast(JumpWork.CloseLoop(label))
            work.addLast(JumpWork.Range(sourceIndex + 1, endIndex))
        }
        is ControlSuperInstruction.If,
        is ControlSuperInstruction.IfCondition,
        -> {
            val label = JumpLabel(targetIndex = null, handlerDepth = handlers.size)
            labels.addLast(label)
            val state = JumpIfState(
                label = label,
                jumpToThenIndex = output.size,
            )
            output.add(JumpIfInstruction(instruction, UNPATCHED_OFFSET))

            val elseIndex = controlFlow.elseIndices[sourceIndex]
            val thenEnd = if (elseIndex >= 0) elseIndex else endIndex
            val elseStart = if (elseIndex >= 0) elseIndex + 1 else endIndex
            work.addLast(JumpWork.CloseIf(state))
            work.addLast(JumpWork.Range(sourceIndex + 1, thenEnd))
            work.addLast(JumpWork.BeginThen(state))
            work.addLast(JumpWork.Range(elseStart, endIndex))
        }
        is ControlInstruction.TryTable -> {
            val pushHandlerIndex = output.size
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
                            instructionIndex = pushHandlerIndex,
                            handlerIndex = handlerIndex,
                        ),
                    )
                }
            }

            val label = JumpLabel(targetIndex = null, handlerDepth = handlers.size + 1)
            labels.addLast(label)
            handlers.addLast(ActiveTryHandler)
            work.addLast(JumpWork.CloseTryTable(label, pushHandlerIndex))
            work.addLast(JumpWork.Range(sourceIndex + 1, endIndex))
        }
        is ControlInstruction.If -> error("raw if reached jump lowering: $instruction")
        else -> error("unsupported jump control opener: $instruction")
    }
}

private fun JumpInstructionLowerer(
    instruction: Instruction,
    output: MutableList<Instruction>,
    labels: ArrayDeque<JumpLabel>,
    handlers: ArrayDeque<ActiveTryHandler>,
    takenPaths: MutableList<TakenPath>,
) {
    when (instruction) {
        is ControlInstruction.Br -> lowerJump(
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
        )
        is ControlInstruction.BrIf -> lowerJumpIf(
            instruction = ControlSuperInstruction.BrIf(
                operand = FusedOperand.ValueStack,
                labelIndex = instruction.labelIndex,
            ),
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlInstruction.BrTable -> lowerJumpTable(
            instruction = ControlSuperInstruction.BrTable(
                operand = FusedOperand.ValueStack,
                labelIndices = instruction.labelIndices,
                defaultLabelIndex = instruction.defaultLabelIndex,
            ),
            labels = labels,
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlInstruction.BrOnNull -> lowerJumpOnNull(
            instruction = ControlSuperInstruction.BrOnNull(
                operand = FusedOperand.ValueStack,
                labelIndex = instruction.labelIndex,
            ),
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlInstruction.BrOnNonNull -> lowerJumpOnNonNull(
            instruction = ControlSuperInstruction.BrOnNonNull(
                operand = FusedOperand.ValueStack,
                labelIndex = instruction.labelIndex,
            ),
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlInstruction.BrOnCast -> lowerJumpOnCast(
            instruction = ControlSuperInstruction.BrOnCast(
                operand = FusedOperand.ValueStack,
                labelIndex = instruction.labelIndex,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
            ),
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlInstruction.BrOnCastFail -> lowerJumpOnCastFail(
            instruction = ControlSuperInstruction.BrOnCastFail(
                operand = FusedOperand.ValueStack,
                labelIndex = instruction.labelIndex,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
            ),
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlSuperInstruction.BrIf -> lowerJumpIf(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlSuperInstruction.BrIfCondition -> lowerJumpIfCondition(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlSuperInstruction.BrTable -> lowerJumpTable(
            instruction = instruction,
            labels = labels,
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlSuperInstruction.BrOnNull -> lowerJumpOnNull(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlSuperInstruction.BrOnNonNull -> lowerJumpOnNonNull(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlSuperInstruction.BrOnCast -> lowerJumpOnCast(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        is ControlSuperInstruction.BrOnCastFail -> lowerJumpOnCastFail(
            instruction = instruction,
            target = jumpTarget(labels, instruction.labelIndex),
            currentHandlerDepth = handlers.size,
            output = output,
            takenPaths = takenPaths,
        )
        else -> output.add(instruction)
    }
}

private fun JumpControlFlowIndex(
    instructions: List<Instruction>,
    endExclusive: Int,
): JumpControlFlow {
    val endIndices = IntArray(instructions.size) { -1 }
    val elseIndices = IntArray(instructions.size) { -1 }
    val openers = ArrayDeque<Int>()

    repeat(endExclusive) { index ->
        val instruction = instructions[index]
        when {
            isStructuredControlOpener(instruction) -> openers.addLast(index)
            instruction is ControlInstruction.Else -> {
                val openerIndex = openers.lastOrNull()
                    ?: error("jump control index found else without an opener at $index")
                require(
                    instructions[openerIndex] is ControlSuperInstruction.If ||
                        instructions[openerIndex] is ControlSuperInstruction.IfCondition,
                ) {
                    "jump control index found else outside an if at $index"
                }
                require(elseIndices[openerIndex] < 0) {
                    "jump control index found duplicate else at $index"
                }
                elseIndices[openerIndex] = index
            }
            instruction is ControlInstruction.End -> {
                require(instruction.count == 1) {
                    "jump lowering requires frame-slot-normalized End(1), found $instruction at $index"
                }
                val openerIndex = openers.removeLastOrNull()
                    ?: error("jump control index found end without an opener at $index")
                endIndices[openerIndex] = index
            }
        }
    }

    require(openers.isEmpty()) {
        "jump control index found unclosed openers: $openers"
    }
    return JumpControlFlow(endIndices, elseIndices)
}

private fun isStructuredControlOpener(instruction: Instruction): Boolean = when (instruction) {
    is ControlInstruction.Block,
    is ControlInstruction.Loop,
    is ControlInstruction.If,
    is ControlInstruction.TryTable,
    is ControlSuperInstruction.If,
    is ControlSuperInstruction.IfCondition,
    -> true
    else -> false
}

private fun isStructuredControlInstruction(instruction: Instruction): Boolean =
    isStructuredControlOpener(instruction) ||
        instruction is ControlInstruction.Else ||
        instruction is ControlInstruction.End

private data class JumpControlFlow(
    val endIndices: IntArray,
    val elseIndices: IntArray,
)

private data class JumpIfState(
    val label: JumpLabel,
    val jumpToThenIndex: Int,
    var jumpPastThenIndex: Int = -1,
)

private sealed interface JumpWork {

    data class Range(
        val start: Int,
        val endExclusive: Int,
    ) : JumpWork

    data class CloseBlock(val label: JumpLabel) : JumpWork

    data class CloseLoop(val label: JumpLabel) : JumpWork

    data class BeginThen(val state: JumpIfState) : JumpWork

    data class CloseIf(val state: JumpIfState) : JumpWork

    data class CloseTryTable(
        val label: JumpLabel,
        val pushHandlerIndex: Int,
    ) : JumpWork
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
    takenPaths: MutableList<TakenPath>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpIf(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
        ),
    )
    addTakenPath(
        takenPaths = takenPaths,
        instructionIndex = instructionIndex,
        instructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
    )
    if (target.targetIndex == null) {
        target.holes.add(OffsetJumpHole(instructionIndex))
    }
}

private fun lowerJumpIfCondition(
    instruction: ControlSuperInstruction.BrIfCondition,
    target: JumpLabel,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
    takenPaths: MutableList<TakenPath>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpIfCondition(
            condition = instruction.condition,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
        ),
    )
    addTakenPath(
        takenPaths = takenPaths,
        instructionIndex = instructionIndex,
        instructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
    )
    if (target.targetIndex == null) {
        target.holes.add(OffsetJumpHole(instructionIndex))
    }
}

private fun JumpIfInstruction(
    instruction: Instruction,
    offset: Int,
): AdminInstruction = when (instruction) {
    is ControlSuperInstruction.If -> AdminInstruction.JumpIf(
        operand = instruction.operand,
        offset = offset,
    )
    is ControlSuperInstruction.IfCondition -> AdminInstruction.JumpIfCondition(
        condition = instruction.condition,
        offset = offset,
    )
    else -> error("unsupported if instruction: $instruction")
}

private fun lowerJumpTable(
    instruction: ControlSuperInstruction.BrTable,
    labels: ArrayDeque<JumpLabel>,
    currentHandlerDepth: Int,
    output: MutableList<Instruction>,
    takenPaths: MutableList<TakenPath>,
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
        ),
    )

    instruction.takenInstructions.forEachIndexed { index, instructions ->
        addTakenPath(
            takenPaths = takenPaths,
            instructionIndex = instructionIndex,
            branchIndex = index,
            instructions = instructions + handlerExitInstructions(currentHandlerDepth, targets[index]),
        )
    }
    addTakenPath(
        takenPaths = takenPaths,
        instructionIndex = instructionIndex,
        branchIndex = null,
        instructions = instruction.defaultTakenInstructions +
            handlerExitInstructions(currentHandlerDepth, defaultTarget),
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
    takenPaths: MutableList<TakenPath>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpOnNull(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
        ),
    )
    addTakenPath(
        takenPaths = takenPaths,
        instructionIndex = instructionIndex,
        instructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
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
    takenPaths: MutableList<TakenPath>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpOnNonNull(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
        ),
    )
    addTakenPath(
        takenPaths = takenPaths,
        instructionIndex = instructionIndex,
        instructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
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
    takenPaths: MutableList<TakenPath>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpOnCast(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
        ),
    )
    addTakenPath(
        takenPaths = takenPaths,
        instructionIndex = instructionIndex,
        instructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
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
    takenPaths: MutableList<TakenPath>,
) {
    val instructionIndex = output.size
    output.add(
        AdminInstruction.JumpOnCastFail(
            operand = instruction.operand,
            offset = target.targetIndex ?: UNPATCHED_OFFSET,
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
        ),
    )
    addTakenPath(
        takenPaths = takenPaths,
        instructionIndex = instructionIndex,
        instructions = instruction.takenInstructions + handlerExitInstructions(currentHandlerDepth, target),
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
    is AdminInstruction.JumpIfCondition -> instruction.copy(offset = targetIndex)
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
    val holes: MutableList<JumpHole> = [],
)

private data object ActiveTryHandler

private sealed interface TakenPath {
    val instructionIndex: Int
    val instructions: List<Instruction>

    fun targetIndex(output: List<Instruction>): Int

    fun patchTarget(output: MutableList<Instruction>, targetIndex: Int)
}

private data class OffsetTakenPath(
    override val instructionIndex: Int,
    override val instructions: List<Instruction>,
) : TakenPath {
    override fun targetIndex(output: List<Instruction>): Int = when (val instruction = output[instructionIndex]) {
        is AdminInstruction.JumpIf -> instruction.offset
        is AdminInstruction.JumpIfCondition -> instruction.offset
        is AdminInstruction.JumpOnNull -> instruction.offset
        is AdminInstruction.JumpOnNonNull -> instruction.offset
        is AdminInstruction.JumpOnCast -> instruction.offset
        is AdminInstruction.JumpOnCastFail -> instruction.offset
        else -> error("unsupported taken-path instruction: $instruction")
    }

    override fun patchTarget(
        output: MutableList<Instruction>,
        targetIndex: Int,
    ) {
        output[instructionIndex] = patchOffsetInstruction(output[instructionIndex], targetIndex)
    }
}

private data class JumpTableTakenPath(
    override val instructionIndex: Int,
    val branchIndex: Int?,
    override val instructions: List<Instruction>,
) : TakenPath {
    override fun targetIndex(output: List<Instruction>): Int {
        val instruction = output[instructionIndex] as AdminInstruction.JumpTable
        return if (branchIndex != null) instruction.offsets[branchIndex] else instruction.defaultOffset
    }

    override fun patchTarget(
        output: MutableList<Instruction>,
        targetIndex: Int,
    ) {
        output[instructionIndex] = patchJumpTableInstruction(output[instructionIndex], branchIndex, targetIndex)
    }
}

private fun addTakenPath(
    takenPaths: MutableList<TakenPath>,
    instructionIndex: Int,
    instructions: List<Instruction>,
) {
    if (instructions.isNotEmpty()) {
        takenPaths.add(OffsetTakenPath(instructionIndex, instructions))
    }
}

private fun addTakenPath(
    takenPaths: MutableList<TakenPath>,
    instructionIndex: Int,
    branchIndex: Int?,
    instructions: List<Instruction>,
) {
    if (instructions.isNotEmpty()) {
        takenPaths.add(JumpTableTakenPath(instructionIndex, branchIndex, instructions))
    }
}

private fun appendTakenPaths(
    output: MutableList<Instruction>,
    takenPaths: List<TakenPath>,
    hasRootLabel: Boolean,
) {
    if (takenPaths.isEmpty()) return

    val skipStubsIndex = if (hasRootLabel) {
        null
    } else {
        output.size.also {
            output.add(AdminInstruction.Jump(offset = UNPATCHED_OFFSET))
        }
    }

    takenPaths.forEach { path ->
        val targetIndex = path.targetIndex(output)
        path.patchTarget(output, output.size)
        output.addAll(path.instructions)
        output.add(AdminInstruction.Jump(offset = targetIndex))
    }

    if (skipStubsIndex != null) {
        output[skipStubsIndex] = AdminInstruction.Jump(offset = output.size)
    }
}

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
