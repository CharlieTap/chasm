package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Index.LocalIndex

fun fusedVariableInstruction(): FusedVariableInstruction = fusedLocalSet()

fun fusedGlobalSet(
    operand: FusedOperand = fusedOperand(),
    globalIdx: Index.GlobalIndex = globalIndex(),
) = FusedVariableInstruction.GlobalSet(
    operand = operand,
    globalIdx = globalIdx,
)

fun fusedLocalSet(
    operand: FusedOperand = fusedOperand(),
    localIdx: LocalIndex = localIndex(),
) = FusedVariableInstruction.LocalSet(
    operand = operand,
    localIdx = localIdx,
)
