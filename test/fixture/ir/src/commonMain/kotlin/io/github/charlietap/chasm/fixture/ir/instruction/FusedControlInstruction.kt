package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Index

fun fusedControlInstruction(): FusedControlInstruction = fusedIf()

fun fusedCall(
    operands: List<FusedOperand> = emptyList(),
    functionIndex: Index.FunctionIndex = functionIndex(),
) = FusedControlInstruction.Call(
    operands = operands,
    functionIndex = functionIndex,
)

fun fusedIf(
    operand: FusedOperand = fusedOperand(),
    blockType: ControlInstruction.BlockType = blockType(),
    thenInstructions: List<Instruction> = emptyList(),
    elseInstructions: List<Instruction>? = null,
) = FusedControlInstruction.If(
    operand = operand,
    blockType = blockType,
    thenInstructions = thenInstructions,
    elseInstructions = elseInstructions,
)
