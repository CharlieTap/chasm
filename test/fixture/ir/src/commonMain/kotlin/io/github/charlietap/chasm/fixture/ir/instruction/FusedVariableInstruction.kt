package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Index.LocalIndex

fun fusedVariableInstruction(): FusedVariableInstruction = fusedLocalSet()

fun fusedGlobalGet(
    globalIdx: Index.GlobalIndex = globalIndex(),
    destination: FusedDestination = valueStackDestination(),
) = FusedVariableInstruction.GlobalGet(
    globalIdx = globalIdx,
    destination = destination,
)

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

fun fusedLocalTee(
    operand: FusedOperand = fusedOperand(),
    localIdx: LocalIndex = localIndex(),
) = FusedVariableInstruction.LocalTee(
    operand = operand,
    localIdx = localIdx,
)
