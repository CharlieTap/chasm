package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.VariableSuperInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Index.LocalIndex

fun fusedVariableInstruction(): VariableSuperInstruction = fusedLocalSet()

fun fusedGlobalGet(
    globalIdx: Index.GlobalIndex = globalIndex(),
    destination: FusedDestination = valueStackDestination(),
) = VariableSuperInstruction.GlobalGet(
    globalIdx = globalIdx,
    destination = destination,
)

fun fusedGlobalSet(
    operand: FusedOperand = fusedOperand(),
    globalIdx: Index.GlobalIndex = globalIndex(),
) = VariableSuperInstruction.GlobalSet(
    operand = operand,
    globalIdx = globalIdx,
)

fun fusedLocalSet(
    operand: FusedOperand = fusedOperand(),
    localIdx: LocalIndex = localIndex(),
) = VariableSuperInstruction.LocalSet(
    operand = operand,
    localIdx = localIdx,
)

fun fusedLocalTee(
    operand: FusedOperand = fusedOperand(),
    localIdx: LocalIndex = localIndex(),
) = VariableSuperInstruction.LocalTee(
    operand = operand,
    localIdx = localIdx,
)
