package io.github.charlietap.chasm.optimiser.passes.controlflow

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.StackAdjustment
import io.github.charlietap.chasm.type.ext.asFunctionType

internal typealias InstructionRewriter = (ControlFlowContext, Instruction, MutableList<Instruction>) -> Unit

internal fun InstructionRewriter(
    context: ControlFlowContext,
    instruction: Instruction,
    output: MutableList<Instruction>,
) = InstructionRewriter(
    context = context,
    instruction = instruction,
    output = output,
    expressionRewriter = ::ExpressionRewriter,
    stackDifferential = ::StackDifferential,
)

internal inline fun InstructionRewriter(
    context: ControlFlowContext,
    instruction: Instruction,
    output: MutableList<Instruction>,
    expressionRewriter: ExpressionRewriter,
    stackDifferential: StackDifferential,
) {
    when (instruction) {
        is ControlInstruction.Br -> {

            val block = context.blocks[instruction.labelIndex.idx]
            val jumpInstruction = AdminInstruction.JumpInstruction.JumpAdjusting(
                offset = context.ip,
                adjustment = block.adjustment,
            )

            block.jumps.add(jumpInstruction)
            context.ip += 1
            output.add(jumpInstruction)
        }
        is ControlInstruction.BrIf -> {

            val block = context.blocks[instruction.labelIndex.idx]
            val jumpInstruction = AdminInstruction.JumpInstruction.JumpIf(
                offset = context.ip,
                adjustment = block.adjustment,
            )

            block.jumps.add(jumpInstruction)
            context.ip += 1
            context.sp += -1
            output.add(jumpInstruction)
        }
        is ControlInstruction.BrOnNull -> {

            val block = context.blocks[instruction.labelIndex.idx]
            val jumpInstruction = AdminInstruction.JumpInstruction.JumpOnNull(
                offset = context.ip,
                adjustment = block.adjustment,
            )

            block.jumps.add(jumpInstruction)
            context.ip += 1
            context.sp += -1
            output.add(jumpInstruction)
        }
        is ControlInstruction.BrOnNonNull -> {

            val block = context.blocks[instruction.labelIndex.idx]
            val jumpInstruction = AdminInstruction.JumpInstruction.JumpOnNonNull(
                offset = context.ip,
                adjustment = block.adjustment,
            )

            block.jumps.add(jumpInstruction)
            context.ip += 1
            context.sp += -1
            output.add(jumpInstruction)
        }
        is ControlInstruction.BrOnCast -> {

            val block = context.blocks[instruction.labelIndex.idx]
            val jumpInstruction = AdminInstruction.JumpInstruction.JumpOnCast(
                offset = context.ip,
                adjustment = block.adjustment,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
            )

            block.jumps.add(jumpInstruction)
            context.ip += 1
            output.add(jumpInstruction)
        }
        is ControlInstruction.BrOnCastFail -> {

            val block = context.blocks[instruction.labelIndex.idx]
            val jumpInstruction = AdminInstruction.JumpInstruction.JumpOnCastFail(
                offset = context.ip,
                adjustment = block.adjustment,
                srcReferenceType = instruction.srcReferenceType,
                dstReferenceType = instruction.dstReferenceType,
            )

            block.jumps.add(jumpInstruction)
            context.ip += 1
            output.add(jumpInstruction)
        }
        is ControlInstruction.BrTable -> {

            val offsets = MutableList(instruction.labelIndices.size) {
                context.ip
            }
            val adjustments = mutableListOf<StackAdjustment>()
            val jumpInstruction = AdminInstruction.JumpInstruction.JumpTable(
                offsets = offsets,
                adjustments = adjustments,
                defaultOffset = context.ip,
                defaultAdjustment = context.blocks[instruction.defaultLabelIndex.idx].adjustment,
            )

            instruction.labelIndices.forEachIndexed { idx, labelIndex ->
                val block = context.blocks[labelIndex.idx]
                block.jumpTables.add(Pair(idx, jumpInstruction))
                adjustments.add(block.adjustment)
            }

            val block = context.blocks[instruction.defaultLabelIndex.idx]
            block.jumps.add(jumpInstruction)

            context.ip += 1
            context.sp += -1
            output.add(jumpInstruction)
        }
        is ControlInstruction.Block -> {

            val ip = context.ip
            val functionType = instruction.blockType.asFunctionType(context.module.definedTypes)
            val blockSp = context.sp - functionType.params.types.size
            val stackAdjustment = StackAdjustment(blockSp, functionType.results.types.size)

            val block = Block(stackAdjustment)
            context.blocks.addFirst(block)

            val expression = expressionRewriter(context, Expression(instruction.instructions))
            output.addAll(expression.instructions)

            val usedBlock = context.blocks.removeFirst()
            updateJumpOffsets(ip, expression.instructions.size, usedBlock)
        }
        is ControlInstruction.If -> {

            context.sp += -1
            val ip = context.ip
            val functionType = instruction.blockType.asFunctionType(context.module.definedTypes)
            val blockSp = context.sp - functionType.params.types.size
            val stackAdjustment = StackAdjustment(blockSp, functionType.results.types.size)

            val block = Block(stackAdjustment)
            context.blocks.addFirst(block)

            context.ip += 1
            val expression = expressionRewriter(context, Expression(instruction.thenInstructions))

            val jumpInstructions = instruction.elseInstructions?.let { 2 } ?: 1
            val jumpIfNotInstruction = AdminInstruction.JumpInstruction.JumpIfNot(
                offset = expression.instructions.size + jumpInstructions,
            )
            output.add(jumpIfNotInstruction)
            output.addAll(expression.instructions)

            instruction.elseInstructions?.let { instructions ->

                context.sp = blockSp + functionType.params.types.size
                context.ip += 1
                val expression = expressionRewriter(context, Expression(instructions))
                val jumpInstruction = AdminInstruction.JumpInstruction.Jump(
                    offset = expression.instructions.size + 1,
                )
                output.add(jumpInstruction)
                output.addAll(expression.instructions)
            }

            val usedBlock = context.blocks.removeFirst()
            updateJumpOffsets(ip, context.ip - ip, usedBlock)
        }
        is ControlInstruction.Loop -> {

            val ip = context.ip
            val functionType = instruction.blockType.asFunctionType(context.module.definedTypes)
            val blockSp = context.sp - functionType.params.types.size
            val stackAdjustment = StackAdjustment(blockSp, functionType.params.types.size)

            val block = Block(stackAdjustment, false)
            context.blocks.addFirst(block)

            val expression = expressionRewriter(context, Expression(instruction.instructions))
            output.addAll(expression.instructions)

            val usedBlock = context.blocks.removeFirst()
            updateJumpOffsets(ip, expression.instructions.size, usedBlock)
        }
        is ControlInstruction.TryTable -> {
            val expression = expressionRewriter(context, Expression(instruction.instructions))

            val rewritten = instruction.copy(
                instructions = buildList {
                    addAll(expression.instructions)
                },
            )
            output.add(rewritten)
        }
        is ControlInstruction.Return -> {
            val block = context.blocks.last()
            output.add(AdminInstruction.ReturnFunction(block.adjustment))
            context.ip += 1
        }
        else -> {
            context.sp += stackDifferential(context, instruction)
            context.ip += 1
            output.add(instruction)
        }
    }
}

internal fun updateJumpOffsets(
    ip: Int,
    blockInstructions: Int,
    block: Block,
) {
    updateJumps(ip, blockInstructions, block)
    updateJumpTables(ip, blockInstructions, block)
}

private fun updateJumps(
    ip: Int,
    blockInstructions: Int,
    block: Block,
) {
    block.jumps.forEach { jumpInstruction ->
        when (jumpInstruction) {
            is AdminInstruction.JumpInstruction.Jump -> {
                jumpInstruction.offset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.offset
                } else {
                    ip - jumpInstruction.offset
                }
            }
            is AdminInstruction.JumpInstruction.JumpAdjusting -> {
                jumpInstruction.offset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.offset
                } else {
                    ip - jumpInstruction.offset
                }
            }
            is AdminInstruction.JumpInstruction.JumpIf -> {
                jumpInstruction.offset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.offset
                } else {
                    ip - jumpInstruction.offset
                }
            }
            is AdminInstruction.JumpInstruction.JumpIfNot -> {
                jumpInstruction.offset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.offset
                } else {
                    ip - jumpInstruction.offset
                }
            }
            is AdminInstruction.JumpInstruction.JumpOnCast -> {
                jumpInstruction.offset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.offset
                } else {
                    ip - jumpInstruction.offset
                }
            }
            is AdminInstruction.JumpInstruction.JumpOnCastFail -> {
                jumpInstruction.offset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.offset
                } else {
                    ip - jumpInstruction.offset
                }
            }
            is AdminInstruction.JumpInstruction.JumpOnNonNull -> {
                jumpInstruction.offset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.offset
                } else {
                    ip - jumpInstruction.offset
                }
            }
            is AdminInstruction.JumpInstruction.JumpOnNull -> {
                jumpInstruction.offset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.offset
                } else {
                    ip - jumpInstruction.offset
                }
            }
            is AdminInstruction.JumpInstruction.JumpTable -> {
                jumpInstruction.defaultOffset = if (block.forwards) {
                    ip + blockInstructions - jumpInstruction.defaultOffset
                } else {
                    ip - jumpInstruction.defaultOffset
                }
            }
        }
    }
}

private fun updateJumpTables(
    ip: Int,
    blockInstructions: Int,
    block: Block,
) {
    block.jumpTables.forEach { (offsetIndex, jumpInstruction) ->
        when (jumpInstruction) {
            is AdminInstruction.JumpInstruction.JumpTable -> {
                val originalOffset = jumpInstruction.offsets[offsetIndex]
                jumpInstruction.offsets[offsetIndex] = if (block.forwards) {
                    ip + blockInstructions - originalOffset
                } else {
                    ip - originalOffset
                }
            }
            else -> throw IllegalStateException(
                "Call to update offsets of jump tables on a non jump table instruction",
            )
        }
    }
}
