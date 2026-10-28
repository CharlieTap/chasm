package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

internal typealias InstructionLinker = (Instruction, Int, LinkContext) -> DispatchableInstruction

internal fun InstructionLinker(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode / 48) {
    0 -> linkInstructions0(instruction, opcode, context)
    1 -> linkInstructions1(instruction, opcode, context)
    2 -> linkInstructions2(instruction, opcode, context)
    3 -> linkInstructions3(instruction, opcode, context)
    4 -> linkInstructions4(instruction, opcode, context)
    5 -> linkInstructions5(instruction, opcode, context)
    6 -> linkInstructions6(instruction, opcode, context)
    7 -> linkInstructions7(instruction, opcode, context)
    8 -> linkInstructions8(instruction, opcode, context)
    9 -> linkInstructions9(instruction, opcode, context)
    10 -> linkInstructions10(instruction, opcode, context)
    11 -> linkInstructions11(instruction, opcode, context)
    12 -> linkInstructions12(instruction, opcode, context)
    else -> when {
        opcode < 665 -> linkInstructions13(instruction, opcode, context)
        opcode < CONTROL_OPCODES -> linkReferenceInstruction(instruction as ReferenceInstruction, context)
        opcode < ADMIN_OPCODES -> linkControlInstruction(instruction as ControlInstruction, context)
        else -> linkAdminInstruction(instruction as AdminInstruction, opcode, context)
    }
}

private const val CONTROL_OPCODES = 672
private const val ADMIN_OPCODES = 684
