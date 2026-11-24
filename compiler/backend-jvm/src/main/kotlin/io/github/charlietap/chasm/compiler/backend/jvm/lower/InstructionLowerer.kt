package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE

internal fun lowerInstruction(
    instruction: Instruction,
    context: LowerContext,
) {
    when (instruction) {
        is NumericInstruction -> lowerNumericInstruction(instruction, context)
        is VariableInstruction -> lowerVariableInstruction(instruction, context)
        is ParametricInstruction -> lowerParametricInstruction(instruction, context)
        is ControlInstruction -> lowerControlInstruction(instruction, context)
        is AdminInstruction -> lowerAdminInstruction(instruction, context)
        is MemoryInstruction -> lowerMemoryInstruction(instruction, context)
        is TableInstruction -> lowerTableInstruction(instruction, context)
        is ReferenceInstruction -> lowerReferenceInstruction(instruction, context)
        is AggregateInstruction -> lowerAggregateInstruction(instruction, context)
    }
}

internal fun LowerContext.lowerFunctionBody(
    frameCost: Int,
) {

    lowerPrologue(frameCost)
    lowerPositions(function.instructions.indices)

    val needsEndTrap = emit.fallsThrough() || function.exceptions != null || hasLabel(function.instructions.size)
    if (needsEndTrap) {
        emit.place(bodyEnd)
        trapUnreachable()
    } else {
        emit.placeWithoutFrame(bodyEnd)
    }

    lowerExceptionHandling()
    lowerOutOfLine()
}

internal fun LowerContext.lowerPositions(positions: IntRange) {
    for (position in positions) {

        enterInstruction(position)
        val label = placement(position)
        emit.place(label)
        markSite(position, label)

        if (slotKindFlow.reached(position)) {
            lowerInstruction(function.instructions[position], this)
        } else if (slotKindFlow.leader(position) || position == positions.first) {
            trapUnreachable()
        }
    }
}

internal fun LowerContext.lowerOutOfLine() {
    outOfLine.forEach { path -> path() }
}

private fun LowerContext.trapUnreachable() = trap(UNREACHABLE)
