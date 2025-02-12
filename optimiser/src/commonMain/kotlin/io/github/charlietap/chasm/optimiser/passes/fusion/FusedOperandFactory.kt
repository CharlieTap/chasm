package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction

internal typealias FusedOperandFactory = (Instruction) -> FusedOperand?

internal fun FusedOperandFactory(
    instruction: Instruction,
): FusedOperand? = when (instruction) {
    is NumericInstruction.I32Const -> FusedOperand.I32Const(instruction.value)
    is NumericInstruction.I64Const -> FusedOperand.I64Const(instruction.value)
    is NumericInstruction.F32Const -> FusedOperand.F32Const(instruction.value)
    is NumericInstruction.F64Const -> FusedOperand.F64Const(instruction.value)
    is VariableInstruction.LocalGet -> FusedOperand.LocalGet(instruction.localIdx)
    is VariableInstruction.GlobalGet -> FusedOperand.GlobalGet(instruction.globalIdx)
    else -> null
}
